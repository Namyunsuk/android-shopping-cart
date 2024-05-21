package woowacourse.shopping.model.data

import woowacourse.shopping.model.Cart
import kotlin.math.min

object CartsImpl : CartDao {
    private const val OFFSET = 1
    private const val EXCEPTION_INVALID_ID = "Cart not found with id: %d"
    private var id: Long = 0
    private val carts = mutableMapOf<Long, Cart>()

    override fun itemSize() = carts.size

    override fun save(cart: Cart): Long {
        val oldCart =
            carts.keys.find { it == cart.id }?.let {
                carts[it]
            }

        if (oldCart == null) {
            carts[id] = cart.copy(id = id)
            return id++
        }
        val count = oldCart.product.count + cart.product.count
        carts.remove(oldCart.id)
        carts[id] = oldCart.copy(product = oldCart.product.copy(count = count))
        return id++
    }

    override fun deleteAll() {
        carts.clear()
    }

    override fun delete(id: Long) {
        carts.remove(id)
    }

    override fun find(id: Long): Cart {
        return carts[id] ?: throw NoSuchElementException(invalidIdMessage(id))
    }

    override fun findAll(): List<Cart> {
        return carts.map { it.value }
    }

    override fun getProducts(
        page: Int,
        pageSize: Int,
    ): List<Cart> {
        val fromIndex = (page - OFFSET) * pageSize
        val toIndex = min(fromIndex + pageSize, carts.size)
        return carts.values.toList().subList(fromIndex, toIndex)
    }

    override fun plusCartCount(cartId: Long) {
        carts[cartId]?.let {
            carts[cartId] = it.copy(product = it.product.inc())
        }
    }

    override fun minusCartCount(cartId: Long) {
        carts[cartId]?.let {
            carts[cartId] = it.copy(product = it.product.dec())
        }
    }

    private fun invalidIdMessage(id: Long) = EXCEPTION_INVALID_ID.format(id)
}
