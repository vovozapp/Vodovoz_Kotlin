import com.vodovoz.app.domain.general.model.CartOperation
import com.vodovoz.app.domain.general.respository.CartManagerRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.domain.general.use_case.AddOrIncrementCartItemUseCase
import com.vodovoz.app.domain.general.use_case.SyncCartDataUseCase
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong


class TestTests {

    private val vodovozServiceRepository: VodovozServiceRepository = mockk()
    private val cartManagerRepository: CartManagerRepository = mockk()

    private val cartVersion: AtomicLong = AtomicLong(0L)
    private var countInCart = AtomicInteger(2)
    private val productId: Long = 0L

    private val syncCartDataUseCase: SyncCartDataUseCase = mockk()

    val addCartUseCase: AddOrIncrementCartItemUseCase = AddOrIncrementCartItemUseCase(
        vodovozServiceRepository,
        cartManagerRepository,
        syncCartDataUseCase
    )

    @Test
    fun `test something`() = runBlocking {
        coEvery { cartManagerRepository.getCartVersion() } returns cartVersion.get()

        coEvery { syncCartDataUseCase(any()) } returnsMany(listOf(
            flow {
                delay(300L)
                Result.success(Unit)
            },
            flow {
                delay(100L)
                Result.failure<Unit>(Throwable())
            },
            flow {
                delay(400L)
                return@flow
            },
            flow {
                delay(600L)
                Result.success(Unit)
            },
            flow {
                delay(300L)
                Result.success(Unit)
            }
        ))

        coEvery { cartManagerRepository.incrementItemQuantity(any()) } returns CartOperation(
            cartVersion.incrementAndGet(),
            productId,
            countInCart.incrementAndGet()
        )
        coEvery {
            vodovozServiceRepository.updateProductInCart(
                productId,
                countInCart.get()
            )
        } returns flow { emit(Result.success("Success")) }

        coEvery { cartManagerRepository.decrementItemQuantity(any(), any()) } returns CartOperation(
            cartVersion.get(),
            productId,
            countInCart.decrementAndGet()
        )

        val jobs = mutableListOf<Job>()
        repeat(4){
            jobs+=launch {
                addCartUseCase(productId).firstOrNull()
            }
        }

        jobs.joinAll()

        println(countInCart.get())
        assert(4 == countInCart.get())

    }
}