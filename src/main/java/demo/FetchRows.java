package demo;

import entity.ResidentCitizen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 *  source : https://stackoverflow.com/questions/36443790/how-to-limit-the-number-of-records-while-reading-from-mysql-table-using-multithr
 *  limit record while reading from msql using multithread
 *
 */



public class FetchRows {

    private static final Logger log = LoggerFactory.getLogger(FetchRows.class);

    public static void main(String[] args) {

        try {
            new FetchRows().print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void print() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        Properties dbProps = new Properties();
        dbProps.setProperty("user", "root");
        dbProps.setProperty("password", "root123");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resident_citizen", dbProps)) {
//            try (Statement st = conn.createStatement()) {
//                prepareTestData(st);
//            }
            // https://stackoverflow.com/a/2448019/3080094
            try (Statement st = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY)) {
                st.setFetchSize(Integer.MIN_VALUE);
                fetchAndPrintTestData(st);
            }
        }
    }

    boolean refreshTestData = false;
    int maxRecords = 5_555;

    void prepareTestData(Statement st) throws SQLException {

        int recordCount = 0;
        if (refreshTestData) {
            st.execute("drop table if exists fetchrecords");
            st.execute("create table fetchrecords (id mediumint not null auto_increment primary key, created timestamp default current_timestamp)");
            for (int i = 0; i < maxRecords; i++) {
                st.addBatch("insert into fetchrecords () values ()");
                if (i % 500 == 0) {
                    st.executeBatch();
                    log.info("{} records available.", i);
                }
            }
            st.executeBatch();
            recordCount = maxRecords;
        } else {
            try (ResultSet rs = st.executeQuery("select count(*) from fetchrecords")) {
                rs.next();
                recordCount = rs.getInt(1);
            }
        }
        log.info("{} records available for testing.", recordCount);
    }

    int batchSize = 1_000;
    int maxBatchesInMem = 3;
    int printFinishTimeoutS = 5;

    void fetchAndPrintTestData(Statement st) throws SQLException, InterruptedException {

        final BlockingQueue<List<ResidentCitizen>> printQueue = new LinkedBlockingQueue<List<ResidentCitizen>>(maxBatchesInMem);
        final PrintToConsole printTask = new PrintToConsole(printQueue); // dequeue
        final PrintToConsole printTask2 = new PrintToConsole(printQueue); // dequeue
        new Thread(printTask).start();
        new Thread(printTask2).start();
        long timeStart = System.currentTimeMillis();
        System.out.println("time start  =" + timeStart);
        try (ResultSet rs = st.executeQuery("select * from resident_citizen")) {
            List<ResidentCitizen> l = new LinkedList<>();
            while (rs.next()) {
                ResidentCitizen bean = new ResidentCitizen();
                bean.setId(rs.getString("id"));
                bean.setPersonalId(rs.getString("personal_id"));
                l.add(bean);
                if (l.size() % batchSize == 0) {
                    /*
                     * The printTask can stop itself when this producer is too slow to put records on the print-queue.
                     * Therefor, also check printTask.isStopping() to break the while-loop.
                     */
                    if (printTask.isStopping()) {
                        throw new TimeoutException("Print task has stopped.");
                    }
                    enqueue(printQueue, l);
//                    l.clear();
                    l = new LinkedList<>();
                }
            }
            if (l.size() > 0) {
                enqueue(printQueue, l);
            }
        } catch (TimeoutException | InterruptedException e) {
            log.error("Unable to finish printing records to console: {}", e.getMessage());
            printTask.stop();
            printTask2.stop();
        } finally {
            log.info("Reading records finished.");
            st.close();
            if (!printTask.isStopping()) {
                try {
                    enqueue(printQueue, Collections.<ResidentCitizen> emptyList());
                } catch (Exception e) {
                    log.error("Unable to signal last record to print.", e);
                    printTask.stop();
                    printTask2.stop();
                }
            }
            if (!printTask.await(printFinishTimeoutS, TimeUnit.SECONDS)) {
                log.error("Print to console task did not finish.");
            }
        }
    }

    int enqueueTimeoutS = 2000;
    // To test a slow printer, see also Thread.sleep statement in PrintToConsole.print.
    // int enqueueTimeoutS = 1;

    void enqueue(BlockingQueue<List<ResidentCitizen>> printQueue, List<ResidentCitizen> l) throws InterruptedException, TimeoutException {

        log.debug("Adding {} records to print-queue.", l.size());
//        printQueue.put(l);
        if (!printQueue.offer(l, enqueueTimeoutS, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException("Unable to put print data on queue within " + enqueueTimeoutS + " seconds.");
        }
    }

    long dequeueTimeoutS = 1l;

    public class PrintToConsole implements Runnable {

        private final BlockingQueue<List<ResidentCitizen>> q;
        private final CountDownLatch finishedLock = new CountDownLatch(1);
        private volatile boolean stop;

        public PrintToConsole(BlockingQueue<List<ResidentCitizen>> q) {
            this.q = q;
        }

        @Override
        public void run() {

            try {
                while (!stop) {
                    List<ResidentCitizen> l = q.poll(dequeueTimeoutS, TimeUnit.SECONDS);
                    if (l == null) {
                        log.error("Unable to get print data from queue within {} seconds.", dequeueTimeoutS);
                        break;
                    }
                    if (l.isEmpty()) {
                        break;
                    }
                    print(l);
                }
                if (stop) {
                    log.error("Printing to console was stopped.");
                }
            } catch (Exception e) {
                log.error("Unable to print records to console.", e);
            } finally {
                if (!stop) {
                    stop = true;
                    log.info("Printing to console finished.");
                }
                finishedLock.countDown();
            }
        }

        void print(List<ResidentCitizen> l) {

            log.info("Got list {} records | One element: {}", l.size(), l.get(1).getId());
            // To test a slow printer, see also enqueueTimeoutS.
//             try { Thread.sleep(1000L); } catch (Exception ignored) {}
        }

        public void stop() {
            stop = true;
        }

        public boolean isStopping() {
            return stop;
        }

        public void await() throws InterruptedException {
            finishedLock.await();
        }

        public boolean await(long timeout, TimeUnit tunit) throws InterruptedException {
            return finishedLock.await(timeout, tunit);
        }

    }

    static class FetchRecordBean {

        private int id;
        private java.util.Date created;

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public java.util.Date getCreated() {
            return created;
        }
        public void setCreated(java.util.Date created) {
            this.created = created;
        }

    }
}