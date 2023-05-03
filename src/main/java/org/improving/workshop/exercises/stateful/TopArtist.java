package org.improving.workshop.exercises.stateful;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.improving.workshop.Streams;

import static org.improving.workshop.Streams.TOPIC_DATA_DEMO_STREAMS;
import static org.improving.workshop.Streams.startStreams;

@Slf4j
public class TopArtist {
    // MUST BE PREFIXED WITH "kafka-workshop-"
    public static final String OUTPUT_TOPIC = "kafka-workshop-top-artist";


    /**
     * The Streams application as a whole can be launched like any normal Java application that has a `main()` method.
     */
    public static void main(final String[] args) {
        final StreamsBuilder builder = new StreamsBuilder();

        // configure the processing topology
        configureTopology(builder);

        // fire up the engines
        startStreams(builder);
    }

    static void configureTopology(final StreamsBuilder builder) {
        builder
                .stream(TOPIC_DATA_DEMO_STREAMS, Consumed.with(Serdes.String(), Streams.SERDE_STREAM_JSON))
                .peek((key, value) -> log.info("Event Received: {},{}", key, value))

                // add topology here
                .map((streamId, stream) -> KeyValue.pair(stream.artistid(), 0L))
                .groupByKey()
                .reduce((aggValue, newValue) -> aggValue + 1, Materialized.as("count-store"))
                .toStream()
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
    }

}