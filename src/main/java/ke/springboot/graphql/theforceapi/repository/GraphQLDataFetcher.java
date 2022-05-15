package ke.springboot.graphql.theforceapi.repository;

import graphql.schema.DataFetcher;
import ke.springboot.graphql.theforceapi.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GraphQLDataFetcher {

    private final String REST_URL = "https://swapi.dev/api/people/";

    @Autowired
    private RestTemplate restTemplate;

    public DataFetcher<List<Person>> getAllPeople(){
        return dataFetchingEnvironment -> {
            return restTemplate
                    .exchange(REST_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<Person>>() {
                    }).getBody();
        };
    }

    public DataFetcher<Person> getPeopleByName(){
        return dataFetchingEnvironment -> {
            String name = dataFetchingEnvironment.getArgument("name");
            return restTemplate.getForObject(REST_URL + "/" + name, Person.class);
        };
    }

    public DataFetcher<Person> getPeopleByPage(){
        return dataFetchingEnvironment -> {
            String page = dataFetchingEnvironment.getArgument("page");
            return restTemplate.getForObject(REST_URL + "/" + page, Person.class);
        };
    }

}
