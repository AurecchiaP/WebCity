package utils;

import com.google.gson.*;
import models.*;

import java.lang.reflect.Type;


/**
 * utility JSON class used to make data on the server into a json string before sending it to the client
 */
public abstract class JSON {


    public static class ClassSerializer implements JsonSerializer<JavaClass> {


        /**
         * defines rules to serialise a JavaClass into a json, needed because of nested classes
         */
        @Override
        public JsonElement serialize(final JavaClass cls, final Type typeOfSrc, final JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("name", context.serialize(cls.getName()));
            jsonObject.add("color", context.serialize(cls.color));
            jsonObject.add("cx", context.serialize(cls.cx));
            jsonObject.add("cy", context.serialize(cls.cy));
            jsonObject.add("cz", context.serialize(cls.cz));
            jsonObject.add("attributes", context.serialize(cls.getAttributes().getValue()));
            jsonObject.add("methods", context.serialize(cls.getMethods().getValue()));
            jsonObject.add("linesOfCode", context.serialize(cls.getLinesOfCode().getValue()));

            // TODO not sure if I want to keep them as single json fields or objects
//            Gson gson = new Gson();
//            jsonObject.add("Attributes", new JsonParser().parse(gson.toJson(cls.getAttributes())));
//            jsonObject.add("Methods", new JsonParser().parse(gson.toJson(cls.getMethods())));
//            jsonObject.add("LinesOfCode", new JsonParser().parse(gson.toJson(cls.getLinesOfCode())));
            return jsonObject;
        }
    }

    /**
     * utility method that takes a JavaPackage and returns it in a json String format, following
     * the serialisation rules defined (aka rules for the structure of the json)
     *
     * @param pkg the package that we want to serialise to a json String
     * @return a json String representing the given package
     */
    public static String toJSON(JavaPackage pkg) {
        final GsonBuilder gsonBuilder = new GsonBuilder();

        // define how to serialise JavaClass objects
        gsonBuilder.registerTypeAdapter(JavaClass.class, new ClassSerializer());
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.create();


        return gson.toJson(pkg);
    }
}
