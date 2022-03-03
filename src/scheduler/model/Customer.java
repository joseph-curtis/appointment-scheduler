package scheduler.model;

public record Customer(int id,
                       String name,
                       String address,
                       String postalCode,
                       String phone,
                       FirstLevelDivision division) {
}
