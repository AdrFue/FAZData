import java.sql.*;
import java.util.List;

public class MySqlHandler {

    private static Connection connect() {

        String url = "jdbc:sqlite:D:\\Dokumente\\SQLite\\test.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void insert() {
        String[] headlines = getHeadlines();
        for (String hl : headlines) {
            if (WebHandler.headline.equals(hl)) return;
        }

        insertArticle();
        insertRessorts();
        insertThemes();
        System.out.println("Inserted into Database Headline " + WebHandler.headline);

    }

    public static void insertArticle() {
        String sql = "INSERT INTO faz_articles(" +
                "fa_headline, fa_emphasis, fa_date, fa_time, fa_author, fa_place, fa_readTime, fa_pages, fa_text, fa_source, fa_comments, fa_link, fa_ts" +
                ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, WebHandler.headline);
            pstmt.setString(2, WebHandler.emphasis);
            pstmt.setString(3, WebHandler.date.toString());
            pstmt.setString(4, WebHandler.time.toString());
            pstmt.setString(5, WebHandler.author);
            pstmt.setString(6, WebHandler.place);
            pstmt.setInt(7, WebHandler.readTime);
            pstmt.setInt(8, WebHandler.pages);
            pstmt.setString(9, WebHandler.text);
            pstmt.setString(10, WebHandler.source);
            pstmt.setBoolean(11, WebHandler.comments);
            pstmt.setString(12, WebHandler.link);
            pstmt.setString(13, new Timestamp(System.currentTimeMillis()).toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void insertRessorts() {
        String sql = "INSERT INTO faz_ressorts(" +
                "fr_fa_id, fr_ressort, fr_depth, fr_depth" +
                ") VALUES(?, ?, ?, ?)";

        int fa_id = getId();
        List<String> ressorts = WebHandler.ressorts;

        for (int i = 0; i < ressorts.size(); i++) {
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, fa_id);
                pstmt.setString(2, ressorts.get(i));
                pstmt.setInt(3, i);
                pstmt.setString(4, new Timestamp(System.currentTimeMillis()).toString());
                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void insertThemes() {
        String sql = "INSERT INTO faz_themes(" +
                "ft_fa_id, ft_theme, ft_ts" +
                ") VALUES(?, ?, ?)";

        int fa_id = getId();
        List<String> themes = WebHandler.themes;

        for (int i = 0; i < themes.size(); i++) {
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, fa_id);
                pstmt.setString(2, themes.get(i));
                pstmt.setString(3, new Timestamp(System.currentTimeMillis()).toString());
                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static int getId() {
        String sql = "SELECT fa_id FROM faz_articles ORDER BY fa_id DESC LIMIT 1;";
        int fa_id = -1;

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                fa_id = rs.getInt("fa_id");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return fa_id;
    }

    public static String[] getHeadlines() {
        String sql = "SELECT fa_headline FROM faz_articles ORDER BY fa_id DESC LIMIT 30;";
        String[] fa_headlines = new String[30];

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            int i = 0;
            while (rs.next()) {
                fa_headlines[i] = rs.getString("fa_headline");
                i++;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return fa_headlines;
    }

    public static String[] getLinks() {
        String sql = "SELECT fa_link FROM faz_articles ORDER BY fa_id DESC LIMIT 20;";
        String[] fa_links = new String[20];

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            int i = 0;
            while (rs.next()) {
                fa_links[i] = rs.getString("fa_link");
                i++;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return fa_links;
    }

    public static String getLastLink() {
        return getLinks()[0];
    }

}
