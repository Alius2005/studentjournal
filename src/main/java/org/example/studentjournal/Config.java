package org.example.studentjournal;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;

public class Config {
    public final String jdbcUrl;
    public final String jdbcUser;
    public final String jdbcPassword;

    public Config(String filePath) throws Exception{
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        Element jdbc = (Element) root.getElementsByTagName("jdbc").item(0);
        this.jdbcUrl = jdbc.getElementsByTagName("url").item(0).getTextContent().trim();
        this.jdbcUser = jdbc.getElementsByTagName("user").item(0).getTextContent().trim();
        this.jdbcPassword = jdbc.getElementsByTagName("password").item(0).getTextContent().trim();
    }
}