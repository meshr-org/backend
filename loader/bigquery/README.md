Q# collector
Collect data sent over HTTP from trackers and publish the data on pubsub.

*package
mvn clean package
mvn package jib:build -Djib.container.environment=HOST="datahem-vkd3zhb3jq-lz.a.run.app",HOST_PORT="443",HOST_URI="/topic/tmp",FREQUENCY="2"
mvn package jib:build -Djib.container.environment=HOST="datahem-vkd3zhb3jq-lz.a.run.app",HOST_PORT=443,HOST_URI="/topic/tmp",FREQUENCY=2,BACKUP_TOPIC="backup",ALLOWED_ORIGINS_PATTERN=".*.",PACKAGE="org.meshr.collector.vertx",VERSION="0.9.0"

*run locally
java -jar target/loader.bigquery-0.9.0.jar -DHTTP_PORT=8080 -DBACKUP_TOPIC=tmp -DHOST=datahem-vkd3zhb3jq-lz.a.run.app -DHOST_PORT=80 -DHOST_URI=/optimize/default/topic/tmp -DFREQUENCY=2 -DVERSION=1.0.0

* test locally
mvn clean package jib:dockerBuild
docker run -p 8080:8080 gcr.io/datahem/loader-bigquery
curl http://localhost:8080/headers
curl --header "Content-Type: application/json"   --request POST   --data '{"data":{"version":"2","items":[{"id":"12345","brand":"Google","name":"Triblend Android T-Shirt","variant":"Gray","category":"Apparel","category2":"Mens","category3":"Shirts","category4":"Tshirts","price":"15.25","quantity":"1","list_id":"SR123","list_name":"Search Results"},{"id":"67890","brand":"Google","name":"Donut Friday Scented T-Shirt","variant":"Black","category":"Apparel","category2":"Mens","category3":"Shirts","category4":"Tshirts","price":"33.75","quantity":"1","list_id":"SR123","list_name":"Search Results"}]}, "attributes":{"namespace":"com.google.analytics.v1","name":"Hit","topic":"com.google.analytics.v1.Hit-collector"}}'   "http://localhost:8080/topic/tmp"

curl --header "Content-Type: application/json"   --request POST   --data '{"data":{"firstname":"testCookie","age":"testValue"}, "attributes":{"namespace":"com.google.analytics.v1","name":"Hit","topic":"com.google.analytics.v1.Hit-collector"}}'   "http://localhost:8080/topic/tmp"

curl --header "Content-Type: application/json"   --request POST   --data '{"v":"2","tid":"G-K8MQEWSD38","gtm":"2oe4f0","_p":"1992368660","sr":"1920x1080","ul":"sv-se","cid":"504172281.1582733768","dl":"https%3A%2F%2Frobertsahlin.com%2F","dr":"https%3A%2F%2Fwww.google.se%2F","dt":"robertsahlin.com","sid":"1587737451","sct":"2","seg":"1","_s":"1","en":"view_item_list","_et":"6","pr1":"nmTriblend%20Android%20T-Shirt~id12345~pr15.25~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaGray~lnSearch%20Results~liSR123~lp1~qt1","pr2":"nmDonut%20Friday%20Scented%20T-Shirt~id67890~pr33.75~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaBlack~lnSearch%20Results~liSR123~lp2~qt1"}' "http://localhost:8080/topic/tmp"



tar -xvf graalvm-ce-java8-linux-amd64-20.0.0.tar.gz
rm graalvm-ce-java8-linux-amd64-20.0.0.tar.gz
export PATH=/home/robert_sahlin/graalvm-ce-java8-20.0.0/bin:$PATH
export JAVA_HOME=/home/robert_sahlin/graalvm-ce-java8-20.0.0/
gu install native-image

tar -xvf graalvm-ce-java11-linux-amd64-20.0.0.tar.gz
rm graalvm-ce-java11-linux-amd64-20.0.0.tar.gz
export PATH=/home/robert_sahlin/graalvm-ce-java11-20.0.0/bin:$PATH
export JAVA_HOME=/home/robert_sahlin/graalvm-ce-java11-20.0.0/
gu install native-image


io.vertx.core.impl.AddressResolver,\

--initialize-at-build-time=io.vertx,\
com.fasterxml.jackson,\
javax,\
org.conscrypt.Conscrypt,\
org.conscrypt.OpenSSLProvider \
io.netty,\

io.netty.buffer,\
io.netty.buffer.PooledByteBufAllocator,\
io.netty.buffer.ByteBufUtil,\
io.netty.buffer.ByteBufAllocator,\

io.netty.channel.DefaultChannelId,\
io.netty.buffer.PooledByteBufAllocator,\
io.netty.util.NetUtil,\
io.netty.util.internal.logging.Log4JLogger,\
io.netty.channel.socket.InternetProtocolFamily,\
io.netty.handler.ssl.ReferenceCountedOpenSslServerContext,\
io.netty.handler.ssl.JdkNpnApplicationProtocolNegotiator,\
io.netty.handler.ssl.ReferenceCountedOpenSslEngine,\
io.netty.handler.ssl.ConscryptAlpnSslEngine,\
io.netty.handler.ssl.JettyNpnSslEngine,\
io.netty.handler.ssl.JettyAlpnSslEngine$ClientEngine,\
io.netty.handler.ssl.JettyAlpnSslEngine$ServerEngine,\
io.netty.handler.ssl.ReferenceCountedOpenSslContext,\
io.netty.handler.ssl.ReferenceCountedOpenSslClientContext,\
io.netty.resolver.HostsFileEntriesResolver,\
io.netty.resolver.dns.DnsNameResolver,\
io.netty.resolver.dns.DnsServerAddressStreamProviders,\
io.netty.resolver.dns.PreferredAddressTypeComparator\$1,\
io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider,\
io.netty.handler.codec.http.websocketx.extensions.compression.DeflateEncoder,\
io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder,\
io.netty.handler.codec.http.HttpObjectEncoder,\
io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,\
io.netty.handler.codec.http2.Http2CodecUtil,\
io.netty.handler.codec.http2.Http2ConnectionHandler,\
io.netty.handler.codec.http2.DefaultHttp2FrameWriter,\
io.netty.util.internal.logging.Log4JLogger,\
io.vertx.core.net.impl.transport.EpollTransport,\
io.vertx.core.net.impl.transport.KQueueTransport,\
io.vertx.core.http.impl.VertxHttp2ClientUpgradeCodec,\
io.vertx.core.eventbus.impl.clustered.ClusteredEventBus,\
com.google.cloud.pubsub.v1 \
--no-fallback \
-H:+UseServiceLoaderFeature \


<!--<buildArgs>-H:+ReportUnsupportedElementsAtRuntime --allow-incomplete-classpath --no-fallback --initialize-at-build-time=io.netty,io.vertx,com.fasterxml.jackson,javax 
        --initialize-at-run-time=io.vertx.core.net.impl.PartialPooledByteBufAllocator,io.netty.handler.codec.http.websocketx.extensions.compression.DeflateEncoder,io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder,io.vertx.core.eventbus.impl.clustered.ClusteredEventBus,io.netty.util.internal.logging.Log4JLogger,io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.codec.http2.Http2CodecUtil,io.netty.handler.codec.http2.DefaultHttp2FrameWriter,io.netty.handler.ssl.ReferenceCountedOpenSslServerContext,io.netty.handler.ssl.JdkNpnApplicationProtocolNegotiator,io.netty.handler.ssl.ReferenceCountedOpenSslEngine,io.netty.handler.ssl.ConscryptAlpnSslEngine,io.netty.handler.ssl.JettyNpnSslEngine,io.netty.handler.ssl.ReferenceCountedOpenSslClientContext,io.vertx.core.net.impl.transport.EpollTransport,io.vertx.core.net.impl.transport.KQueueTransport,io.vertx.core.http.impl.VertxHttp2ClientUpgradeCodec,io.netty.handler.codec.http2.Http2ConnectionHandler
       -H:+UseServiceLoaderFeature 
       -H:IncludeResources=(META-INF|static|webroot|template)/.* 
       -H:ReflectionConfigurationResources=${.}/reflection.json</buildArgs>-->


gcloud projects add-iam-policy-binding datahem \
     --member=serviceAccount:service-271873991144@gcp-sa-pubsub.iam.gserviceaccount.com \
     --role=roles/iam.serviceAccountTokenCreator

gcloud iam service-accounts create cloud-run-pubsub-invoker \
     --display-name "Cloud Run Pub/Sub Invoker"

gcloud run services add-iam-policy-binding processor-cloudrun-ga \
   --member=serviceAccount:cloud-run-pubsub-invoker@datahem.iam.gserviceaccount.com \
   --role=roles/run.invoker

gcloud pubsub subscriptions create ua233405661-processor --topic ua233405661 \
   --push-endpoint=https://processor-cloudrun-ga-vkd3zhb3jq-ew.a.run.app/ \
   --push-auth-service-account=cloud-run-pubsub-invoker@datahem.iam.gserviceaccount.com

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"username":"xyz","password":"xyz"}' \
  http://localhost:8080/topic/tmp

*create docker image and push to container registry
gcloud builds submit --config=cloudbuild.yaml . --substitutions=_VERSION=0.9.0

# Version

## 0.1.0 (2020-02-10): Cloud Run Vert.x
Initial Cloud Run Vert.x collector with eventbus and asyncronous PublisherVerticle.
Added license.
Added config options for setting backup topic.
Added modes for reliable or optimistic collection to optimize latency.