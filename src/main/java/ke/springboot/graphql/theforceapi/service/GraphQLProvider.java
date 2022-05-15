package ke.springboot.graphql.theforceapi.service;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class GraphQLProvider {

    private GraphQL graphQL;

    @Autowired
    private GraphQLDataFetcher dataFetcher;

    @PostConstruct
    public void init() throws IOException {
        final Resource resource = new ClassPathResource("people.graphql");
        String sdl = null;
        try {
            sdl = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }
    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }
    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("people", dataFetcher.getAllPeople()))
                .type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("person", dataFetcher.getPeopleByName()))
                .type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("people", dataFetcher.getPeopleByPage()))
//                .type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("addWebsite", dataFetcher.addWebsite()))
//                .type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("updateWebsite",
//                        dataFetcher.updateWebsite()))
//                .type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("deleteWebsite",
//                        dataFetcher.deleteWebsite()))
                .build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

}
