/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License 1.1.
 * 
 * Parameters
 * 
 * Licensor:             Robert Sahlin
 * Licensed Work:        Meshr
 *                       The Licensed Work is (c) 2020 Robert Sahlin.
 * Additional Use Grant: You may use the Licensed Work when the Licensed Work is 
 *                       processing less than 10 Million unique events per month, 
 *                       provided that you do not use the Licensed Work for a 
 *                       commercial offering that allows third parties to access
 *                       the functionality of the Licensed Work so that such third
 *                       parties directly benefit from the features of the Licensed Work.
 * 
 * Change Date:          12 months after the git commit date of the code
 * 
 * Change License:       GNU AFFERO GENERAL PUBLIC LICENSE, Version 3
 * 
 * For information about alternative licensing arrangements for the Licensed Work,
 * please contact the licensor.
 */

package org.meshr.loader.bigquery;

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
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;

import java.io.InputStream;
import java.nio.channels.Channels;
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
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.message.BinaryMessageDecoder;
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

    public static Schema getAvroSchemaFromCloudStorage(String bucketName, String fileName) throws Exception {
            try{
                LOG.info(bucketName);
                LOG.info(fileName);
                Storage storage = StorageOptions.getDefaultInstance().getService();
                Blob blob = storage.get(BlobId.of(bucketName, fileName));
                //LOG.info("cloud storage got blob");
                ReadChannel reader = blob.reader();
                //LOG.info("cloud storage got reader");
                InputStream inputStream = Channels.newInputStream(reader);
                //String inputStream = new String(blob.getContent()); // ok if small files
                //LOG.info("cloud storage got inputstream");
                Schema schema = new Schema.Parser().parse(inputStream);
                return schema;
            }catch (Exception e){
                LOG.info("cloud storage error");
                e.printStackTrace();
                return null;
            }
        }

    @Override
    public BigQueryService insertData(JsonObject body, String topic, Handler<AsyncResult<Void>> resultHandler) {
        try{
            LOG.info("Trying...");        
            //String json = "{ \"firstname\":\"Frank\", \"age\":\"47\"}";
            //JsonObject entity = body.getJsonObject("data").put("attributes", body.getJsonObject("attributes"));
            //String json = entity.toString();
            LOG.info("body: " + body.toString());
            //String body.getJsonObject("message").getString("data");
            byte[] avro = Base64.getDecoder().decode(body.getJsonObject("message").getString("data"));
            LOG.info(avro);
            //byte[] avro = body.getJsonObject("data").getBinary("data");
            String bucketName = "datahem-schemas";
            String fileName = "com.google.analytics.v2.Event.avsc";
            Schema schema = Schema.create(Schema.Type.STRING);
                schema = getAvroSchemaFromCloudStorage(bucketName, fileName);
                LOG.info(schema.toString());

            
            //JsonAvroConverter converter = new JsonAvroConverter();
            //GenericData.Record record = converter.convertToGenericDataRecord(json.getBytes(), schema);
            //BinaryMessageDecoder<Record> decoder = new BinaryMessageDecoder<>(GenericData.get(), schema, cache);
            BinaryMessageDecoder<Record> decoder = new BinaryMessageDecoder<>(GenericData.get(), schema);
            Record record = decoder.decode(avro);
            TableSchema newSchema = BigQueryAvroUtils.getTableSchema(schema);
            TableRow tRow = BigQueryAvroUtils.convertGenericRecordToTableRow(record, newSchema); 
            LOG.info(tRow.toString());

            HttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            GoogleCredential credential;
            String projectId = "datahem";
            String datasetId = "analytics_2404202019";
            String tableId = "events";
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

                    
                    Bigquery bigquery = new Bigquery.Builder(transport, jsonFactory, credential)
                    .setApplicationName("loader.bigquery").build();
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
        } catch (java.io.IOException e) {
            LOG.error("IO exception decoding record ", e);
        } catch (java.lang.Exception e) {
            LOG.error("Cloud Storage Client contextInitialized error ", e);
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