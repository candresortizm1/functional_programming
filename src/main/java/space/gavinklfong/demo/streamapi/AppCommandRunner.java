package space.gavinklfong.demo.streamapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppCommandRunner implements CommandLineRunner {

	@Autowired
	private CustomerRepo customerRepos;
	
	@Autowired
	private OrderRepo orderRepos;
	
	@Autowired
	private ProductRepo productRepos;

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		/*
		
		log.info("Customers:");
		customerRepos.findAll()
				.forEach(c -> log.info(c.toString()));

		log.info("Orders:");
		orderRepos.findAll()
				.forEach(o -> log.info(o.toString()));

		log.info("Products:");

		productRepos.findAll()
				.forEach(p -> log.info(p.toString()));


		 */

		log.info("----------------------------------");
		log.info("--------------EXERCISES-----------");
		log.info("----------------------------------");

		//Obtain a list of products belongs to category “Books” with price > 100
		log.info("*************Exercise 1***********");
		productRepos.findAll()
				.stream()
				.filter(p -> p.getCategory().equalsIgnoreCase("Books"))
				.filter(p -> p.getPrice() > 100)
				.collect(Collectors.toList()).forEach(p -> log.info(p.toString()));

		//Obtain a list of product with category = “Toys” and then apply 10% discount
		log.info("*************Exercise 3***********");
		productRepos.findAll()
				.stream()
				.filter(p -> p.getCategory().equalsIgnoreCase("Toys"))
				.map(p -> p.withPrice(p.getPrice() * 0.9))
				.collect(Collectors.toList()).forEach(p -> log.info(p.toString()));

		//Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021
		log.info("*************Exercise 4***********");
		orderRepos.findAll()
				.stream()
				.filter(order -> order.getCustomer().getTier() == 2)
				.filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021,2,1)))
				.filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021,4,1)))
				.flatMap(order -> order.getProducts().stream()).distinct()
				.collect(Collectors.toList()).forEach(p -> log.info(p.toString()));

		//Get the 3 most recent placed order
		log.info("*************Exercise 6***********");
		orderRepos.findAll()
				.stream()
				.sorted(Comparator.comparing((Order::getOrderDate)).reversed())
				.limit(3)
				.collect(Collectors.toList()).forEach(p -> log.info(p.toString()));

		//Calculate total lump sum of all orders placed in Feb 2021
		log.info("*************Exercise 8***********");
		log.info(""+orderRepos.findAll()
				.stream()
				.filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021,2,1)))
				.filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021,3,1)))
				.flatMap(order -> order.getProducts().stream())
				.mapToDouble(product-> product.getPrice()).sum());

		//Obtain a collection of statistic figures (i.e. sum, average, max, min, count) for all products of category “Books”
		log.info("*************Exercise 10***********");
		DoubleSummaryStatistics statistics = productRepos.findAll()
				.stream()
				.filter(product -> product.getCategory().equalsIgnoreCase("Books"))
				.mapToDouble(product -> product.getPrice())
				.summaryStatistics();
		log.info(String.format("count = %1$d, average = %2$f, max = %3$f, min = %4$f, sum = %5$f",
				statistics.getCount(), statistics.getAverage(), statistics.getMax(), statistics.getMin(), statistics.getSum()));

		//Produce a data map with order records grouped by customer
		log.info("*************Exercise 12***********");
		orderRepos.findAll()
				.stream()
				.collect(Collectors.groupingBy(order -> order.getCustomer()))
				.forEach((client,order) -> log.info(client.toString()+"->"+order.toString()));


		//Produce a data map with order record and product total sum
		log.info("*************Exercise 13***********");
		orderRepos.findAll()
				.stream()
				.collect(
					Collectors.toMap(
						Function.identity(),
							order -> order.getProducts().stream()
							.mapToDouble(p -> p.getPrice()).sum()
					)
				).forEach((order,total) -> log.info(order.getId()+"->"+total.toString()));

		//Produce a data map with order record and product total sum
		log.info("*************Exercise 13***********");
		orderRepos.findAll()
				.stream()
				.collect(
						Collectors.toMap(
								Function.identity(),
								order -> order.getProducts().stream()
										.mapToDouble(p -> p.getPrice()).sum()
						)
				).forEach((order,total) -> log.info(order.getId()+"->"+total.toString()));

		//Get the most expensive product by category
		log.info("*************Exercise 15***********");
		productRepos.findAll()
				.stream()
				.collect(
						Collectors.groupingBy(
								Product::getCategory,
								Collectors.maxBy(Comparator.comparing(Product::getPrice))))
				.forEach((category,product) -> log.info(category+"->"+product.get()));

	}

}
