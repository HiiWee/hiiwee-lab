module expenses.readers {
    requires org.apache.httpcomponents.httpClient;
    requires java.base;

    exports com.example.expenses.readers;
    exports com.example.expenses.readers.file;
    exports com.example.expenses.readers.http;
}