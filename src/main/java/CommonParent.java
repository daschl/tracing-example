import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.uber.jaeger.Configuration;
import io.opentracing.Tracer;

public class CommonParent {

  public static void main(String... args) throws Exception {

    Tracer tracer = new Configuration(
      "common_parent",
      new Configuration.SamplerConfiguration("const", 1),
      new Configuration.ReporterConfiguration(
        true, "localhost", 5775, 1000, 10000)
    ).getTracer();

    CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().tracer(tracer).build();
    Cluster cluster = CouchbaseCluster.create(env);
    cluster.authenticate("Administrator", "password");
    Bucket bucket = cluster.openBucket("default");


    bucket.upsert(JsonDocument.create("mydoc_id", JsonObject.empty()));
    bucket.get("mydoc_id");

    Thread.sleep(10000);
  }

}
