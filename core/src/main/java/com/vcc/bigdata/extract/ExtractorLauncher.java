package com.vcc.bigdata.extract;

import com.vcc.bigdata.common.config.Configuration;
import com.vcc.bigdata.common.utils.Reflects;
import com.vcc.bigdata.common.utils.Strings;
import com.vcc.bigdata.common.utils.Utils;
import com.vcc.bigdata.extract.mongo.FbGroupPostExtractor;
import com.vcc.bigdata.metric.MetricExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ExtractorLauncher {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        Logger logger = LoggerFactory.getLogger(ExtractorLauncher.class);

        if (args.length == 0) {
            logger.info("Run in development mode");
            startExtractors(
                    new FbGroupPostExtractor(config)
            );
        } else {
            logger.info("Run in server mode, start extractors: " + Strings.join(Arrays.asList(args), " "));
            Extractor[] extractors = new Extractor[args.length];
            for (int i = 0; i < args.length; i++) {
                extractors[i] = Reflects.newInstance(args[i], new Class<?>[]{Configuration.class}, config);
            }
            startExtractors(extractors);
        }

        MetricExporter metricExporter = new MetricExporter(config.toSubProperties("extract"));
        metricExporter.start();

        Utils.addShutdownHook(() -> {
            logger.info("Stop metric exporter server...");
            metricExporter.stop();
            logger.info("Metric exporter stopped");
        });

        metricExporter.join();
    }

    static void startExtractors(Extractor... extractors) {
        for (Extractor extractor : extractors) {
            new Thread(extractor).start();
        }
    }
}
