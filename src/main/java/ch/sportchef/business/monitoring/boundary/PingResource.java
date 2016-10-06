package ch.sportchef.business.monitoring.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("pings")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {
	
	@GET
	@Path("/echo/{echo}")
	public String echo(@PathParam("echo") String param) {
		return param;
	}
	
}
