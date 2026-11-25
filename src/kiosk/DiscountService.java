package kiosk;

import java.util.Optional;

final class DiscountService {
    Optional<DiscountPolicy> student(String number) {
        StudentDiscount policy = new StudentDiscount(number);
        return policy.isVerified() ? Optional.of(policy) : Optional.empty();
    }

    Optional<DiscountPolicy> coupon(String code) {
        CouponDiscount policy = new CouponDiscount(code);
        return policy.isValid() ? Optional.of(policy) : Optional.empty();
    }
}
