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

'use strict';

const express = require('express');

const {PubSub} = require('@google-cloud/pubsub');
const pubsub = new PubSub();

const uuidv4 = require('uuid/v4');

const queryStringParser = require('query-string');

// Create an Express object
const app = express();
app.set('trust proxy', true);
app.post('/topic/:topic/', apiPost);
exports.com_google_analytics_v2_Event = app;

function transform(request){
    var messages = [];
    var payloads = [];
    //console.log(Buffer.from(request.body.message.data, 'base64').toString().trim().replace(/\n/g, "\\n"));
    const data = JSON.parse(Buffer.from(request.body.message.data, 'base64').toString().trim().replace(/\n/g, "\\n"));
    const attributes = request.body.message.attributes;

    if (data.payload) {
        data.payload.split("\n").forEach(function(e) {
            console.log(data.queryString);
            payloads.push(Object.assign(queryStringParser.parse(e), data.queryString));
        });
    } else {
        payloads.push(data.queryString);
    }

    /*** START transformation ***/ 
    payloads.forEach(function(payload) {
        var message = {};
        
        message.attributes = typeof attributes  !== 'undefined' ?  Object.assign({}, attributes)  : {};
        message.attributes.transformTimestamp = new Date().toISOString();
        message.attributes.transformUuid = uuidv4();
        message.attributes.transformTopic = request.params.topic;

        message.data = {
            version : payload.v,
            event_name : payload.en,
            items : Object.keys(payload)
                .filter( key => key.match(/^pr([0-9]{1,3})/) )
                .reduce(function (r, a) {
                    r.push(payload[a]);
                    return r;
                },[])
                .map(x => {
                    var y = x.split('~').reduce(function (r, a) {
                        r[a.slice(0,2)] = a.slice(2);
                        return r;
                    },{});
                    return {
                        id : y.id,
                        brand : y.br,
                        name : decodeURIComponent(y.nm),
                        variant : y.va,
                        category : y.ca,
                        category2 : y.v0,
                        category3 : y.v1,
                        category4 : y.v2,
                        category5 : y.v3,
                        price : y.pr,
                        quantity : y.qt,
                        list_id : y.li,
                        list_name : decodeURIComponent(y.ln)
                    };
                })
        };
        messages.push(message);
    });
    
    /*** STOP transformation ***/
    
    return messages;
}

async function publish(request, response){
    // Pubsub topics to publish message on
    var topic = request.params.topic;
    //var topics =[topic];

    //transform request payload
    var messages = transform(request); 
    
    // Publish to topic
    let messageIds = await Promise.all(
        messages.map(async message => { 
            return pubsub
                .topic(topic)
                .publish(Buffer.from(JSON.stringify(message.data)), message.attributes)
        }))
        .catch(function(err) {
            console.error(err.message);
            response.status(400).end(`error when publishing data object to pubsub`);
        });
    console.log(messageIds);
}

// Collect pubsub push request (POST), transform it and publish data on pubsub
async function apiPost(request, response) {
    if (!request.body) {
        const errorMsg = 'no Pub/Sub message received';
        console.error(`error: ${errorMsg}`);
        response.status(400).end(`Bad Request: ${errorMsg}`);
    }
    if (!request.body.message) {
        const errorMsg = 'invalid Pub/Sub message format';
        console.error(`error: ${errorMsg}`);
        response.status(400).end(`Bad Request: ${errorMsg}`);
    }
    if (!request.body.message.data) {
        const errorMsg = 'invalid Pub/Sub message data';
        console.error(`error: ${errorMsg}`);
        response.status(400).end(`Bad Request: ${errorMsg}`);
    }
    try{
        await publish(request, response);
        if(!response.headersSent){
            response.status(204).end();
            //response.status(200).json(transform(request));
        }
    }catch (error) {
        console.error(error);
        if(!response.headersSent){
            response.status(400).end();
        }
    }
}