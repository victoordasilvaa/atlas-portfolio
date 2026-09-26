import java.nio.file.*;

public class StartServer {
  static String value(String key) {
    String v = System.getenv(key);
    if (v == null || v.isEmpty()) throw new IllegalStateException("Missing " + key);
    return v.replace("&", "&amp;")
        .replace("\"", "&quot;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
  }

  public static void main(String[] args) throws Exception {
    String users =
        "<?xml version=\"1.0\"?><tomcat-users><role rolename=\"editor\"/><role"
            + " rolename=\"viewer\"/><role rolename=\"directory\"/>";
    if (System.getenv("APP_USER") != null) {
      users +=
          "<user username=\""
              + value("APP_USER")
              + "\" password=\""
              + value("APP_PASSWORD")
              + "\" roles=\"editor,viewer\"/>";
      users +=
          "<user username=\""
              + value("VIEWER_USER")
              + "\" password=\""
              + value("VIEWER_PASSWORD")
              + "\" roles=\"viewer\"/>";
    } else
      users +=
          "<user username=\""
              + value("MEMBERS_USER")
              + "\" password=\""
              + value("MEMBERS_PASSWORD")
              + "\" roles=\"directory\"/>";
    Files.writeString(
        Path.of(System.getenv("CATALINA_HOME"), "conf", "tomcat-users.xml"),
        users + "</tomcat-users>");
  }
}
