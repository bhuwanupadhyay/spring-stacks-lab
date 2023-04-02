package org.acme.quickstart;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/awesome")
public class AwesomeResource {

    @Inject
    AwesomeService awesomeService;

    // Add a REST endpoint to find record by id
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAwesome(@PathParam(value = "id") Long id) {
        Awesome awesome = awesomeService.getAwesome(id);
        return Response.ok(awesome).build();
    }

    // Add a REST endpoint to create a record in the database
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAwesome(Awesome awesome) {
        Awesome newAwesome = awesomeService.createAwesome(awesome);
        return Response.ok(newAwesome).build();
    }

    // Add a REST endpoint to update a record in the database
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAwesome(@PathParam(value = "id") Long id, Awesome awesome) {
        Awesome updatedAwesome = awesomeService.updateAwesome(id, awesome);
        return Response.ok(updatedAwesome).build();
    }

    // Add a REST endpoint to delete a record in the database
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAwesome(@PathParam(value = "id") Long id) {
        awesomeService.deleteAwesome(id);
        return Response.ok().build();
    }

    // Add a REST endpoint to list all records in the database
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
        List<Awesome> awesomeList = awesomeService.listAll();
        return Response.ok(awesomeList).build();
    }
}
