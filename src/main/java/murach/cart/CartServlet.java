package murach.cart;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;
import murach.business.User;
import murach.data.ProductIO;
import murach.data.UserIO;
import murach.util.CookieUtil;
import murach.util.MailUtilGmail;

import java.io.IOException;

public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext sc = getServletContext();

        // Get the current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";
        }

        String url = "/index.jsp"; // Default URL
        switch (action) {
            case "add":
                url = handleCart(request, sc);// Handle cart logic
                break;
            case "checkout":
                url = checkout(request, sc);
                break;
            case "update":
                updateCartItem(request, sc);
                url = "/cart.jsp";
                break;
            case "removeItem":
                removeCartItem(request, sc);
                url = "/cart.jsp";
                break;
            case "registerUser":
                url = registerUser(request, response, sc);
                break;
            case "logout":
                url = logOut(request, response);
                break;
            case "confirm":
                url = emailConfirm(request, response,sc);
                break;
            default:
                url = "/index.jsp";
                break;
        }


        // Forward to the appropriate page
        sc.getRequestDispatcher(url).forward(request, response);
    }

    private String emailConfirm(HttpServletRequest request, HttpServletResponse response, ServletContext sc) {
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        Cart cart = (Cart) session.getAttribute("cart");
        String emailAddress = CookieUtil.getCookieValue(cookies, "emailCookie");

        System.out.println(emailAddress);

        double total = cart.getTotalAmount(cart);
        try {
                // Build email content
            String emailBody = MailUtilGmail.buildOrderDetailsEmail(cart, total);

                // Send the email
            MailUtilGmail.sendMail(
                        emailAddress,
                        "vokimanh980@gmail.com", // Sender's email
                        "Order Confirmation",
                        emailBody,
                        true // Email body is HTML
            );
            request.setAttribute("message", "Order confirmation email sent successfully to " + emailBody);
            System.out.println("Sending email to: " + emailAddress);
        } catch (MessagingException e) {
            log("Unable to send email: " + e.getMessage());
        }

        return "/confirm.jsp";

    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
         doPost(request, response); // Delegate to doPost
    }

    private String logOut(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0); //delete the cookie
            cookie.setPath("/"); //allow the download application to access it
            response.addCookie(cookie);
        }

        HttpSession session = request.getSession();
        session.removeAttribute("cart"); // Remove cart from session

        // Xóa thông tin người dùng khỏi session
        session.removeAttribute("user");
         return "/index.jsp";

    }

    private String registerUser(HttpServletRequest request,HttpServletResponse response, ServletContext sc) {
        // get the user data
        HttpSession session = request.getSession();

        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        // store the data in a User object
        User user = new User(email, firstName, lastName);  // Prefer immutable data structures

        // write the User object to a file
        String path = sc.getRealPath("/WEB-INF/EmailList.txt");
        UserIO.add(user, path);

        // store the User object as a session attribute
        session.setAttribute("user", user);

        // add a cookie that stores the user's email to the browser
        Cookie c = new Cookie("emailCookie", email);
        c.setMaxAge(60 * 60 * 24 * 365 * 2); // set age to 2 years
        c.setPath("/"); // allow entire app to access it
        response.addCookie(c);

        String url = "/cart.jsp";

        return url;
    }

    private String checkUser(HttpServletRequest request, ServletContext sc) {
        String url ="";
        String productCode = request.getParameter("productCode");
        HttpSession session = request.getSession();
        session.setAttribute("productCode", productCode);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            Cookie[] cookies = request.getCookies();
            String emailAddress = CookieUtil.getCookieValue(cookies, "emailCookie");

            // if cookie doesn't exist, go to Registration page
            if (emailAddress == null || emailAddress.equals("")) {
                url = "/register.jsp";
            }
            else {
                String path = sc.getRealPath("/WEB-INF/EmailList.txt");
                user = UserIO.getUser(emailAddress, path);
                if (user != null) {
                    session.setAttribute("user", user);
                    url = "/cart.jsp";
                } else {
                    url = "/register.jsp";
                }
            }
        }
        else {
            url = "/cart.jsp";
        }
        return url;
    }

    private String checkout(HttpServletRequest request, ServletContext sc) {
        String url = "";
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        String message = "";

        if (cart == null || cart.getItems().isEmpty()) {
            message = "Your cart is empty. Please add items before checking out.";
            request.setAttribute("message", message);
            url = "/cart.jsp";
        } else {
            message = "Proceeding to checkout...";
            request.setAttribute("message", message);
            request.setAttribute("cart", cart);
            double total = cart.getTotalAmount(cart);
            request.setAttribute("cartTotal", total);

            url = "/checkout.jsp";
        }
        return url;
    }

    private void updateCartItem(HttpServletRequest request, ServletContext sc) {
        String productCode = request.getParameter("productCode");
        String quantityString = request.getParameter("quantity");

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        int quantity;
        try {
            quantity = Integer.parseInt(quantityString);
            if (quantity < 0) {
                quantity = 1; // Đảm bảo không có số lượng âm
            }
        } catch (NumberFormatException e) {
            quantity = 1; // Số lượng mặc định nếu nhập sai
        }

        String path = sc.getRealPath("/WEB-INF/products.txt");
        Product product = ProductIO.getProduct(productCode, path);

        if (product != null) {
            LineItem existingItem = cart.findItemByProductCode(productCode);

            if (existingItem != null) {
                if (quantity == 0) {
                    cart.removeItem(existingItem); // Xóa sản phẩm nếu số lượng là 0
                } else {
                    cart.updateItem(product, quantity); // Cập nhật số lượng
                }
            } else if (quantity > 0) {
                cart.addItem(new LineItem(product, quantity)); // Thêm sản phẩm mới
            }
        }
    }

    private void removeCartItem(HttpServletRequest request, ServletContext sc) {
        String productCode = request.getParameter("productCode");
        String quantityString = request.getParameter("quantity");

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        LineItem itemToRemove = cart.findItemByProductCode(productCode);
        if (itemToRemove != null) {
            cart.removeItem(itemToRemove);
        }
    }


    private String handleCart(HttpServletRequest request, ServletContext sc) throws ServletException, IOException {
        String url = "/cart.jsp"; // Default redirect for cart
        // Retrieve the session and the cart
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        String checkUserUrl = checkUser(request, sc); // This checks the user status
        if (checkUserUrl.equals("/register.jsp")) {
            // If the user is not logged in, redirect to the registration page
            url = addToCart(request, sc);
            return checkUserUrl;
        }  else {
            url = addToCart(request, sc);
        }

        return url; // Return the URL for forwarding
    }

    private String addToCart(HttpServletRequest request, ServletContext sc) {
        // Retrieve the session and the cart
        String url = "/index.jsp";
        HttpSession session = request.getSession();
        String productCode = request.getParameter("productCode");
        String quantityString = request.getParameter("quantity");

        Cart cart = (Cart) session.getAttribute("cart");

        // Check for existing cart, if not create a new one
        if (cart == null) {
            cart = new Cart();
        }

        int quantity = 1; // Default quantity
        try {
            quantity = Integer.parseInt(quantityString);
            if (quantity < 0) {
                quantity = 1; // Ensure no negative quantity
            }
        } catch (NumberFormatException e) {
            // Use default quantity if input is invalid
        }

        String path = sc.getRealPath("/WEB-INF/products.txt");
        Product product = ProductIO.getProduct(productCode, path);

        if (product != null && quantity > 0) {
            cart.addItem(new LineItem(product, quantity));
        }

        session.setAttribute("cart", cart);
        return url = "/cart.jsp";
    }

}
