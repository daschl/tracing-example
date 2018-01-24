import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.uber.jaeger.Configuration;
import io.opentracing.Tracer;

public class SimpleReadWrite {

  public static void main(String... args) throws Exception {

    // init the jaeger tracer

    Tracer tracer = new Configuration(
      "simple_read_write",
      new Configuration.SamplerConfiguration("const", 1),
      new Configuration.ReporterConfiguration(
        true, "localhost", 5775, 1000, 10000)
    ).getTracer();


    // normal couchbase setup, passing in the jaeger tracer env

    CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().tracer(tracer).build();
    Cluster cluster = CouchbaseCluster.create(env, "10.142.171.101");
    cluster.authenticate("Administrator", "password");
    Bucket bucket = cluster.openBucket("default");

    // do a upsert and get

    bucket.upsert(JsonDocument.create("mydoc_id", JsonObject.empty()));
    bucket.get("mydoc_id");

    // dont die immediately so jaeger gets a chance to send the spans to the server

    Thread.sleep(10000);
  }
}
