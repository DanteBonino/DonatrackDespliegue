package server.Routers;

import static io.javalin.apibuilder.ApiBuilder.before;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.apibuilder.EndpointGroup;
import java.util.List;

public class Router implements EndpointGroup, WithSimplePersistenceUnit {
  private final List<EndpointGroup> routers;

  public Router(List<EndpointGroup> routers) {
    this.routers = routers;
  }

  @Override
  public void addEndpoints() {
    before(_ctx -> entityManager().clear());
    routers.forEach(EndpointGroup::addEndpoints);
  }
}