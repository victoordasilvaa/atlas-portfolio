package dev.portfolio.control;

import dev.portfolio.domain.*;
import dev.portfolio.dto.*;
import dev.portfolio.facade.PortfolioFacade;
import dev.portfolio.integration.MemberDirectory;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {
  @Context private ServletContext context;
  @Context private SecurityContext security;

  private PortfolioFacade facade() {
    return (PortfolioFacade) context.getAttribute(PortfolioFacade.class.getName());
  }

  private String actor() {
    return security.getUserPrincipal().getName();
  }

  @GET
  @Path("projects")
  public PageDto<ProjectDto> list(
      @QueryParam("name") String name,
      @QueryParam("status") ProjectStatus status,
      @DefaultValue("0") @QueryParam("page") int page,
      @DefaultValue("20") @QueryParam("size") int size) {
    return facade().snapshot(s -> s.list(name, status, page, size));
  }

  @GET
  @Path("projects/{id}")
  public Response get(@PathParam("id") long id) {
    return response(facade().execute(s -> s.get(id)), 200);
  }

  @POST
  @Path("projects")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(ProjectInput input, @Context UriInfo uri) {
    ProjectDto d = facade().execute(s -> s.create(input, actor()));
    return Response.created(uri.getAbsolutePathBuilder().path(d.id.toString()).build())
        .tag(Integer.toString(d.version))
        .entity(d)
        .build();
  }

  @PUT
  @Path("projects/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@PathParam("id") long id, ProjectInput input) {
    return response(facade().execute(s -> s.update(id, input, actor())), 200);
  }

  @PATCH
  @Path("projects/{id}/status")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response status(@PathParam("id") long id, StatusInput input) {
    return response(facade().execute(s -> s.transition(id, input, actor())), 200);
  }

  @DELETE
  @Path("projects/{id}")
  public Response delete(@PathParam("id") long id, @HeaderParam("If-Match") String match) {
    if (match == null)
      throw new BusinessException(
          "VERSION_REQUIRED", "Envie If-Match com a versão entre aspas.", 428);
    if (!match.matches("\"[0-9]{1,9}\"")) throw new BadRequestException("If-Match inválido.");
    int version = Integer.parseInt(match.substring(1, match.length() - 1));
    facade()
        .execute(
            s -> {
              s.delete(id, version, actor());
              return null;
            });
    return Response.noContent().build();
  }

  @GET
  @Path("portfolio/report")
  public PortfolioReport report() {
    return facade().snapshot(s -> s.report());
  }

  @GET
  @Path("members")
  public java.util.List<MemberDto> members() {
    return ((MemberDirectory) context.getAttribute(MemberDirectory.class.getName())).list();
  }

  private Response response(ProjectDto d, int status) {
    return Response.status(status).tag(Integer.toString(d.version)).entity(d).build();
  }
}
