<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Product Cart</title>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
  <script src="https://cdn.tailwindcss.com"></script> <!-- Import Tailwind CSS -->
  <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>
<div >
  <h1>Cart Details</h1>
  <div>
    <table>
      <thead>
      <tr>
        <th>Description</th>
        <th>Price</th>
        <th>Quantity</th>
        <th>Amount</th>
      </tr>
      </thead>
      <body>
      <c:forEach var="item" items="${cart.items}">
        <tr>
          <td><c:out value="${item.product.description}" /></td>
          <td>${item.product.priceCurrencyFormat}</td>
          <td><c:out value="${item.quantity}" /></td>
          <td>${item.totalCurrencyFormat}</td>
        </tr>
      </c:forEach>
      </body>

      <tr>
        <td class="font-semibold">Total:</td>
        <td></td> <td></td>
        <td >$<c:out value="${cartTotal}"/></td>
      </tr>

    </table>
  </div>
</div>

<form action="cart" method="post">
  <input type="hidden" name="action" value="shop">
  <input type="submit" value="Continue shopping">
</form><br>

<form action=cart method="post">
  <input type="hidden" name="action" value="confirm">
  <input type="submit" value="Confirm">
</form>
</body>
</html>
