package murach.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Cart implements Serializable {
    private ArrayList<LineItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public ArrayList<LineItem> getItems() {
        return items;
    }

    public int getCount() {
        return items.size();
    }

    public void addItem(LineItem item) {
        String productCode = item.getProduct().getCode();

        // Kiểm tra nếu sản phẩm đã có trong giỏ
        for (LineItem existingItem : items) {
            if (existingItem.getProduct().getCode().equals(productCode)) {
                // Cộng dồn số lượng nếu sản phẩm đã có
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }

        // Nếu sản phẩm chưa có, thêm mới mà không cộng số lượng
        items.add(item);
    }

    public void removeItem(LineItem item) {
        String code = item.getProduct().getCode();

        for (int i = 0; i < items.size(); i++) {
            LineItem lineItem = items.get(i);

            if (lineItem.getProduct().getCode().equals(code)) {
                items.remove(i);  // Remove the item if found
                return;
            }
        }
    }

    public void updateItem(Product product, int newQuantity) {
        for (LineItem item : items) {
            if (item.getProduct().getCode().equals(product.getCode())) {
                item.setQuantity(newQuantity);
                return;
            }
        }
    }
    public LineItem findItemByProductCode(String productCode) {
        for (LineItem item : items) {
            if (item.getProduct().getCode().equals(productCode)) {
                return item;
            }
        }
        return null;
    }

    public double getTotalAmount(Cart cart) {
        double total = 0.0;

        for (LineItem item : cart.getItems()) {
            total += item.getTotal();
        }
        BigDecimal roundedTotal = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        return roundedTotal.doubleValue();
    }

}
