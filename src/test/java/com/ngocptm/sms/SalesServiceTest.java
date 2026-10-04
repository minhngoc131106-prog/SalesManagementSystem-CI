/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.ngocptm.sms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author legion
 */
public class SalesServiceTest {
  
private SalesService service;

    @BeforeEach
    void setUp() {
        service = new SalesService();
    }

    // ==========================================
    // 1. TEST CHO CLASS PRODUCT (Để phủ 100% Product.java)
    // ==========================================

    @Test
    @DisplayName("Product Exception: price <= 0")
    void testProduct_InvalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", "Laptop", 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", "Laptop", -50, 1));
    }

    @Test
    @DisplayName("Product Exception: quantity <= 0")
    void testProduct_InvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", "Laptop", 100.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", "Laptop", 100.0, -2));
    }

    // ==========================================
    // 2. TEST CHO CALCULATE SUBTOTAL
    // ==========================================

    @Test
    @DisplayName("Subtotal: Tính chuẩn đơn giá x số lượng")
    void testCalculateSubtotal_Normal() {
        Product p = new Product("P01", "Laptop", 500.0, 3);
        assertEquals(1500.0, service.calculateSubtotal(p), 0.001);
    }

    @Test
    @DisplayName("Subtotal: Số lượng = 1")
    void testCalculateSubtotal_SingleQuantity() {
        Product p = new Product("P02", "Mouse", 250.0, 1);
        assertEquals(250.0, service.calculateSubtotal(p), 0.001);
    }

    @Test
    @DisplayName("Subtotal Exception: Product bị null")
    void testCalculateSubtotal_NullProduct() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateSubtotal(null));
    }

    // ==========================================
    // 3. TEST CHO CALCULATE DISCOUNT (BOUNDARY & MID-RANGE)
    // ==========================================

    @Test
    @DisplayName("Discount Boundary: 999.99 (< 1000) -> 0%")
    void testCalculateDiscount_Boundary_999_99() {
        assertEquals(0.0, service.calculateDiscount(999.99), 0.001);
    }

    @Test
    @DisplayName("Discount Boundary: 1000 (1000 - <5000) -> 5%")
    void testCalculateDiscount_Boundary_1000() {
        assertEquals(50.0, service.calculateDiscount(1000.0), 0.001);
    }

    @Test
    @DisplayName("Discount Boundary: 4999.99 (1000 - <5000) -> 5%")
    void testCalculateDiscount_Boundary_4999_99() {
        assertEquals(249.9995, service.calculateDiscount(4999.99), 0.001);
    }

    @Test
    @DisplayName("Discount Boundary: 5000 (5000 - <10000) -> 10%")
    void testCalculateDiscount_Boundary_5000() {
        assertEquals(500.0, service.calculateDiscount(5000.0), 0.001);
    }

    @Test
    @DisplayName("Discount Boundary: 9999.99 (5000 - <10000) -> 10%")
    void testCalculateDiscount_Boundary_9999_99() {
        assertEquals(999.999, service.calculateDiscount(9999.99), 0.001);
    }

    @Test
    @DisplayName("Discount Boundary: 10000 (>= 10000) -> 15%")
    void testCalculateDiscount_Boundary_10000() {
        assertEquals(1500.0, service.calculateDiscount(10000.0), 0.001);
    }

    @Test
    @DisplayName("Discount Exception: Subtotal âm")
    void testCalculateDiscount_NegativeSubtotal() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateDiscount(-100.0));
    }

    // Task 3: Parameterized Test
    @ParameterizedTest
    @CsvSource({
        "999.99, 0",
        "1000, 50",
        "5000, 500",
        "10000, 1500"
    })
    @DisplayName("Discount: Parameterized Test")
    void testCalculateDiscount_Parameterized(double subtotal, double expected) {
        assertEquals(expected, service.calculateDiscount(subtotal), 0.001);
    }

    // ==========================================
    // 4. TEST CHO CALCULATE SHIPPING FEE
    // ==========================================

    @Test
    @DisplayName("Shipping: Subtotal < 2000 -> Phí 50")
    void testCalculateShippingFee_LessThan2000() {
        assertEquals(50.0, service.calculateShippingFee(1999.99), 0.001);
    }

    @Test
    @DisplayName("Shipping: Subtotal = 2000 (>= 2000) -> Phí 0")
    void testCalculateShippingFee_Equals2000() {
        assertEquals(0.0, service.calculateShippingFee(2000.0), 0.001);
    }

    @Test
    @DisplayName("Shipping: Subtotal > 2000 -> Phí 0")
    void testCalculateShippingFee_GreaterThan2000() {
        assertEquals(0.0, service.calculateShippingFee(5000.0), 0.001);
    }

    @Test
    @DisplayName("Shipping Exception: Subtotal âm")
    void testCalculateShippingFee_NegativeSubtotal() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateShippingFee(-50.0));
    }

    // ==========================================
    // 5. TEST CHO CALCULATE TOTAL
    // ==========================================

    @Test
    @DisplayName("Total: Có phí ship và có discount")
    void testCalculateTotal_WithShippingAndDiscount() {
        Product p = new Product("P01", "Item A", 500.0, 3);
        assertEquals(1475.0, service.calculateTotal(p), 0.001);
    }

    @Test
    @DisplayName("Total: Freeship và discount 10%")
    void testCalculateTotal_Freeship() {
        Product p = new Product("P02", "Item B", 1000.0, 5);
        assertEquals(4500.0, service.calculateTotal(p), 0.001);
    }

    @Test
    @DisplayName("Total Exception: Product null")
    void testCalculateTotal_NullProduct() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateTotal(null));
    }

    // ==========================================
    // 6. TEST CHO CLASSIFY CUSTOMER
    // ==========================================

    @Test
    @DisplayName("Customer Type: Total < 1000 -> REGULAR")
    void testClassifyCustomer_Regular() {
        assertEquals("REGULAR", service.classifyCustomer(999.99));
    }

    @Test
    @DisplayName("Customer Type: 1000 <= Total < 5000 -> SILVER")
    void testClassifyCustomer_Silver() {
        assertEquals("SILVER", service.classifyCustomer(1000.0));
    }

    @Test
    @DisplayName("Customer Type: 5000 <= Total < 10000 -> GOLD")
    void testClassifyCustomer_Gold() {
        assertEquals("GOLD", service.classifyCustomer(5000.0));
    }

    @Test
    @DisplayName("Customer Type: Total >= 10000 -> VIP")
    void testClassifyCustomer_Vip() {
        assertEquals("VIP", service.classifyCustomer(10000.0));
    }

    @Test
    @DisplayName("Customer Type Exception: Total âm")
    void testClassifyCustomer_NegativeTotal() {
        assertThrows(IllegalArgumentException.class, () -> service.classifyCustomer(-10.0));
    }
}
