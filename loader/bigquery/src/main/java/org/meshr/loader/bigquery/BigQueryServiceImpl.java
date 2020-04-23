package org.meshr.loader.bigquery;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.CompositeFuture;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.Vertx;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import com.google.cloud.ServiceOptions;

import com.google.common.cache.LoadingCache;
import com.google.cloud.pubsub.v1.Publisher;

import  java.util.Base64;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;

import java.io.IOException;

//import org.datahem.protobuf.measurementprotocol.v2.*;
//import com.google.pubsub.v1.ProjectTopicName;
//import com.google.pubsub.v1.PubsubMessage;

//import org.meshr.processor.measurementprotocol.v2.utils.*;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TimePartitioning;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.bigquery.model.ErrorProto;
import com.google.api.services.bigquery.Bigquery.Tables;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TimePartitioning;
import com.google.api.services.bigquery.model.Clustering;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import tech.allegro.schema.json2avro.converter.AvroConversionException;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;


//import org.apache.beam.sdk.io.gcp.bigquery.BigQueryAvroUtils;
//import org.meshr.processor.utils.ProtobufUtils;


class BigQueryServiceImpl implements BigQueryService {

  private static final Logger LOG = LoggerFactory.getLogger(BigQueryServiceImpl.class);

    //LoadingCache<String, Publisher> publisherCache;
    JsonObject config;
    WebClient client;
    Random rand;
    //String backupTopic;

    BigQueryServiceImpl(
        JsonObject config,
        WebClient client,
        Handler<AsyncResult<BigQueryService>> readyHandler) {
            LOG.info("BigQuery service ...");    
            this.config = config;
            this.client = client;
            //this.backupTopic = config.getString("BACKUP_TOPIC");
            this.rand = new Random();
            readyHandler.handle(Future.succeededFuture(this));
    }

    @Override
    public BigQueryService insertData(JsonObject body, String topic, Handler<AsyncResult<Void>> resultHandler) {
        LOG.info("Trying...");        
        String json = "{ \"name\":\"Frank\", \"age\":47}";
        String SCHEMA_STR_V1 = "{\"type\":\"record\", \"namespace\":\"foo\", \"name\":\"Man\", \"fields\":[ { \"name\":\"name\", \"type\":\"string\" }, { \"name\":\"age\", \"type\":[\"null\",\"double\"] } ] }";
        Schema schema = new Schema.Parser().parse(SCHEMA_STR_V1);
        JsonAvroConverter converter = new JsonAvroConverter();
        GenericData.Record record = converter.convertToGenericDataRecord(json.getBytes(), schema);
        TableSchema newSchema = BigQueryAvroUtils.getTableSchema(schema);
        TableRow tRow = BigQueryAvroUtils.convertGenericRecordToTableRow(record, newSchema); 

        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential;
        String projectId = "datahem";
                String datasetId = "processor";
                String tableId = "vertx";
        try {
            credential = GoogleCredential.getApplicationDefault(transport,jsonFactory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (credential.createScopedRequired()) {
            credential = credential.createScoped(BigqueryScopes.all());
        }
        try{        
            
    // -->        Optional<GenericRecord> genericRow = Optional.ofNullable(genericRecordFromPayload(body));
            //if(measurementProtocol.isPresent()){
    // -->      TableRow tRow = ... genericRow.get()
            if(true){
                TableDataInsertAllRequest.Rows row = new TableDataInsertAllRequest.Rows();
                row.setInsertId(String.valueOf(System.currentTimeMillis()));
                row.setJson(tRow);
                TableDataInsertAllRequest request = new TableDataInsertAllRequest();
                request.setRows(Arrays.asList(row));

                
                Bigquery bigquery = new Bigquery.Builder(transport, jsonFactory, credential).build();
			    //.setApplicationName(this.config.applicationName).build();
                TableDataInsertAllResponse response = bigquery.tabledata().insertAll(projectId, datasetId, tableId, request).execute();
                if(response.getInsertErrors() != null){
                    for (TableDataInsertAllResponse.InsertErrors err: response.getInsertErrors()) {
                        for (ErrorProto ep: err.getErrors()) {
                            LOG.error(ep.getReason() + " : " + ep.getMessage() + " at " + ep.getLocation());
                        }
                    }
                }	
            }
            resultHandler.handle(Future.succeededFuture());
        } catch (java.lang.Exception e) {
            LOG.error("BigQueryClient contextInitialized error ", e);
                Bigquery.Tables bqTables = new Bigquery.Builder(transport, jsonFactory, credential).build().tables();
    // -->            TableSchema newSchema = ... 
                Table pTable = new Table();
                pTable.setSchema(newSchema);
                pTable.setTableReference(new TableReference()
                        .setProjectId(projectId)
                        .setDatasetId(datasetId)
                        .setTableId(tableId));
                try{
                    LOG.info("Get current table schema");
                    
                    Bigquery.Tables.Get bqTableGet = bqTables.get(projectId, datasetId, tableId);
                    Table bqTable = bqTableGet.execute();
                    TableSchema oldSchema = bqTable.getSchema();
                    if(oldSchema.equals(newSchema)){ //if old schema equals new schema do nothing
                        LOG.info("Current schema equals new schema");
                    }else{ //if old schema doesn't equals new schema, patch the table
                        LOG.info("Current schema doesn't equal new schema -> patch table schema");
                        Bigquery.Tables.Patch bqTablePatch = bqTables.patch(projectId, datasetId, tableId, pTable);
                        bqTablePatch.execute();
                    }
                }
                catch (GoogleJsonResponseException gjre) {
                    if(e instanceof GoogleJsonResponseException){
                        int statusCode = gjre.getStatusCode();
                        if(statusCode == 404){// && table.createDisposition.equals("CREATE_IF_NEEDED")){
                            LOG.info("Couldn't find table -> create new table");
                            LOG.info("create new table " + projectId +":"+datasetId);
                            
                            try{
                                LOG.info("create new table schema: " + pTable.toPrettyString());
                                Bigquery.Tables.Insert bqTableInsert = bqTables.insert(projectId, datasetId, pTable);
                                bqTableInsert.execute();
                            }catch(IOException ioe){
                                LOG.error("exception", ioe);
                            }
                        }else{
                            gjre.printStackTrace();
                            //System.exit(1);
                        }
                    }
                }catch(IOException ioe){
                    LOG.error("exception", ioe);
                }
            resultHandler.handle(Future.failedFuture(e));
        }
        //keepAlive();
    return this;
  }

    private void keepAlive(){
        if(config.getInteger("FREQUENCY") > 0 && rand.nextInt(config.getInteger("FREQUENCY"))==0){ 
            client
            .post(config.getInteger("HOST_PORT", 443), config.getString("HOST"), config.getString("HOST_URI"))
            .ssl(true)
            .timeout(1000)
            .putHeader("Content-Type", "application/json")
            .as(BodyCodec.jsonObject())
            .sendJsonObject(new JsonObject()
                .put("id", config.getString("PROJECT_ID"))
                .put("package", config.getString("PACKAGE"))
                .put("version", config.getString("VERSION"))
                , ar -> {
                    if(ar.succeeded()) {
                        LOG.info("Keep alive status " + ar.result().statusCode());
                    }else {
                        LOG.warn("Keep alive warn " + ar.cause().getMessage());
                    }
            });    
        }
    }
}