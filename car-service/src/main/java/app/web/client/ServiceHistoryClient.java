package app.web.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "service-history", url = "${service.history.url}")
public interface ServiceHistoryClient {
 //   @GetMapping("/api/service-history/car/{carId}")
  //  List<ServiceHistory> getHistory(@PathVariable UUID carId);

  //  @PostMapping("/api/service-history")
   // ServiceHistory createRecord(@RequestBody ServiceHistory record);

   // @GetMapping("/api/service-history/car/{carId}/completed")
   // List<ServiceHistory> getCompletedServices(@PathVariable UUID carId);
}
