package com.datacollection.serde;

public interface Serialization<T> {

    Serializer<T> serializer();

    Deserializer<T> deserializer();

    static <T> Serialization<T> create(String name, Class<T> tClass) {
        switch (name) {
            case "json":
                return new Serialization<T>() {
                    @Override
                    public Serializer<T> serializer() {
                        return new JsonSerializer<>(tClass);
                    }

                    @Override
                    public Deserializer<T> deserializer() {
                        return new JsonDeserializer<>(tClass);
                    }
                };
            default:
                throw new IllegalArgumentException("Invalid serialization name");
        }
    }
}
