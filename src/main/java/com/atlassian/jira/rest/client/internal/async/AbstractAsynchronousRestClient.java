package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.httpclient.api.EntityBuilder;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rest.client.internal.json.JsonParser;
import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Function;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * This is a base class for asynchronous REST clients.
 *
 * @since v2.0
 */
public abstract class AbstractAsynchronousRestClient {

    private static final String JSON_CONTENT_TYPE = "application/json";

    private final HttpClient client;

    protected AbstractAsynchronousRestClient(HttpClient client) {
        this.client = client;
    }

    protected interface ResponseHandler<T> {
        T handle(Response request) throws JSONException, IOException;
    }

    protected final <T> Promise<T> getAndParse(final URI uri, final JsonParser<?, T> parser) {
        return callAndParse(client.newRequest(uri).get(), parser);
    }

    protected final <I, T> Promise<T> postAndParse(final URI uri, I entity, final JsonGenerator<I> jsonGenerator,
                                                final JsonObjectParser<T> parser) {
        final ResponsePromise responsePromise = client.newRequest(uri)
                .setEntity(toEntity(jsonGenerator, entity))
                .post();
        return callAndParse(responsePromise, parser);
    }

    protected final <T> Promise<T> postAndParse(final URI uri, final JSONObject entity, final JsonObjectParser<T> parser) {
        final ResponsePromise responsePromise = client.newRequest(uri)
                .setEntity(entity.toString())
                .setContentType(JSON_CONTENT_TYPE)
                .post();
        return callAndParse(responsePromise, parser);
    }

    protected final Promise<Void> post(final URI uri, final String entity) {
        final ResponsePromise responsePromise = client.newRequest(uri)
                .setEntity(entity)
                .setContentType(JSON_CONTENT_TYPE)
                .post();
        return call(responsePromise);
    }

    protected final Promise<Void> post(final URI uri, final JSONObject entity) {
        return post(uri, entity.toString());
    }

    protected final <T> Promise<Void> post(final URI uri, final T entity, final JsonGenerator<T> jsonGenerator) {
        final ResponsePromise responsePromise = client.newRequest(uri)
                .setEntity(toEntity(jsonGenerator, entity))
                .post();
        return call(responsePromise);
    }

    protected final Promise<Void> post(final URI uri) {
        return post(uri, "");
    }

    protected final <I, T> Promise<T> putAndParse(final URI uri, I entity, final JsonGenerator<I> jsonGenerator,
                                                   final JsonObjectParser<T> parser) {
        final ResponsePromise responsePromise = client.newRequest(uri)
                .setEntity(toEntity(jsonGenerator, entity))
                .put();
        return callAndParse(responsePromise, parser);
    }

    protected final Promise<Void> delete(final URI uri) {
        final ResponsePromise responsePromise = client.newRequest(uri).delete();
        return call(responsePromise);
    }

    protected final <T> Promise<T> callAndParse(final ResponsePromise responsePromise, final ResponseHandler<T> responseHandler) {
        final Function<Response, ? extends T> transformFunction = toFunction(responseHandler);

        return responsePromise.<T>transform()
                .ok(transformFunction)
                .created(transformFunction)
                .others(AbstractAsynchronousRestClient.<T>errorFunction())
                .toPromise();
    }

    @SuppressWarnings("unchecked")
    protected final <T> Promise<T> callAndParse(final ResponsePromise responsePromise, final JsonParser<?, T> parser) {
        final ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
            @Override
            public T handle(Response response) throws JSONException, IOException {
                final String body = response.getEntity();
                return (T) (parser instanceof JsonObjectParser ?
                        ((JsonObjectParser) parser).parse(new JSONObject(body)) :
                        ((JsonArrayParser) parser).parse(new JSONArray(body)));
            }
        };
        return callAndParse(responsePromise, responseHandler);
    }

    protected final Promise<Void> call(final ResponsePromise responsePromise) {
        return responsePromise.<Void>transform()
                .ok(constant((Void) null))
                .created(constant((Void) null))
                .noContent(constant((Void) null))
                .others(AbstractAsynchronousRestClient.<Void>errorFunction())
                .toPromise();
    }

    protected HttpClient client() {
        return client;
    }

    private static <T> Function<Response, T> errorFunction()
    {
        return new Function<Response, T>()
        {
            @Override
            public T apply(Response response)
            {
                try
                {
                    final String body = response.getEntity();
                    final Collection<String> errorMessages = extractErrors(body);
                    throw new RestClientException(errorMessages, response.getStatusCode());
                }
                catch (JSONException e)
                {
                    throw new RestClientException(e, response.getStatusCode());
                }
            }
        };
    }

    private static <T> Function<Response, ? extends T> toFunction(final ResponseHandler<T> responseHandler)
    {
        return new Function<Response, T>()
        {
            @Override
            public T apply(@Nullable Response input)
            {
                try
                {
                    return responseHandler.handle(input);
                }
                catch (JSONException e)
                {
                    throw new RestClientException(e);
                }
                catch (IOException e)
                {
                    throw new RestClientException(e);
                }
            }
        };
    }

    private static <T> Function<Response, T> constant(final T value)
    {
        return new Function<Response, T>()
        {
            @Override
            public T apply(Response input)
            {
                return value;
            }
        };
    }


    static Collection<String> extractErrors(String body) throws JSONException
    {
        if (body == null)
        {
            return Collections.emptyList();
        }
        JSONObject jsonObject = new JSONObject(body);
        final Collection<String> errorMessages = new ArrayList<String>();
        final JSONArray errorMessagesJsonArray = jsonObject.optJSONArray("errorMessages");
        if (errorMessagesJsonArray != null)
        {
            errorMessages.addAll(JsonParseUtil.toStringCollection(errorMessagesJsonArray));
        }
        final JSONObject errorJsonObject = jsonObject.optJSONObject("errors");
        if (errorJsonObject != null)
        {
            final JSONArray valuesJsonArray = errorJsonObject.toJSONArray(errorJsonObject.names());
            if (valuesJsonArray != null)
            {
                errorMessages.addAll(JsonParseUtil.toStringCollection(valuesJsonArray));
            }
        }
        return errorMessages;
    }

    private <T> EntityBuilder toEntity(final JsonGenerator<T> generator, final T bean)
    {
        return new EntityBuilder()
        {

            @Override
            public Entity build()
            {
                return new Entity()
                {
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        return Collections.singletonMap("Content-Type", JSON_CONTENT_TYPE);
                    }

                    @Override
                    public InputStream getInputStream()
                    {
                        try
                        {
                            return new ByteArrayInputStream(generator.generate(bean).toString().getBytes(Charset.forName("UTF-8")));
                        }
                        catch (JSONException e)
                        {
                            throw new RestClientException(e);
                        }
                    }
                };
            }
        };
    }
}
