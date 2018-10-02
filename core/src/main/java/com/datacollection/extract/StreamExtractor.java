package com.datacollection.extract;

import com.datacollection.common.config.Configuration;
import com.datacollection.common.utils.Strings;
import com.datacollection.entity.Event;

public abstract class StreamExtractor<TSource> extends Extractor {

    public StreamExtractor(String group, Configuration config) {
        super(group, config);
    }

    @Override
    protected void onLoop() {
        try (DataStream<TSource> stream = openDataStream()) {
            while (stream.hasNext() && isNotCanceled()) {
                TSource tSource = stream.next();
                try {
                    sendEvent(extractData(tSource), tSource);
                } catch (Throwable t) {
                    String s = Strings.firstCharacters(tSource.toString(), 200);
                    logger.warn("Extract data failed " + s, t);
                }
            }
        }
        if (isNotCanceled()) logger.info("All data processed");
    }

    protected abstract Event extractData(TSource source);

    protected abstract DataStream<TSource> openDataStream();
}
