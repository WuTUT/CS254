package edu.vandy.simulator.managers.palantiri.spinLockHashMap

import admin.AssignmentTests
import admin.injectInto
import edu.vandy.simulator.utils.Student.Type.Undergraduate
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier
import kotlin.random.Random

/**
 * Run with power mock and prepare the Thread
 * class for mocking it's static methods.
 */
@ExperimentalCoroutinesApi
class Assignment_1B_SpinLockTest : AssignmentTests() {
    @MockK
    lateinit var isCancelled: Supplier<Boolean>

    @MockK
    lateinit var owner: AtomicBoolean

    @SpyK
    internal var spinLock = SpinLock()

    private val responses: List<Boolean> =
            MutableList(Random.nextInt(10, 20)) {
                false
            }.apply {
                add(true)
            }.toList()

    @Before
    fun before() {
        owner.injectInto(spinLock)
        runAs(Undergraduate)
    }

    @Test
    fun `tryLock uses correct calls`() {
        every { owner.compareAndSet(false, true) } returns true
        assertTrue(spinLock.tryLock())
        verify { owner.compareAndSet(false, true) }
        confirmVerified(owner)
    }

    @Test
    fun `locks when not already locked`() {
        every { spinLock.tryLock() } returns true
        every { owner.get() } returns false
        spinLock.lock(isCancelled)
        verify(exactly = 1) { spinLock.tryLock() }
        verify(exactly = 1) { owner.get() }
        confirmVerified(owner, isCancelled)
    }

    @Test
    fun `lock only calls tryLock when required`() {
        every { spinLock.tryLock() } returns true
        every { owner.get() } returnsMany responses.map { !it }
        every { isCancelled.get() } returns false
        spinLock.lock(isCancelled)
        verify(exactly = 1) { spinLock.tryLock() }
        verify(exactly = responses.size) { owner.get() }
        verify(exactly = responses.size - 1) { isCancelled.get() }
        confirmVerified(owner, isCancelled)
    }

    @Test
    fun `lock spins until acquired`() {
        every { spinLock.tryLock() } returnsMany responses
        every { isCancelled.get() } returns false
        every { owner.get() } returns false
        spinLock.lock(isCancelled)
        verify(exactly = responses.size) {
            spinLock.tryLock()
            owner.get()
        }
        verify(exactly = responses.size - 1) {
            isCancelled.get()
        }
        confirmVerified(owner, isCancelled)
    }

    @Test
    fun `lock spins until cancelled`() {
        every { spinLock.tryLock() } returns false
        every { isCancelled.get() } returnsMany responses
        every { owner.get() } returns false
        assertThrows<CancellationException>("Should throw a CancellationException") {
            spinLock.lock(isCancelled)
        }
        verify(exactly = responses.size) {
            spinLock.tryLock()
            owner.get()
        }
        verify(exactly = responses.size) {
            isCancelled.get()
        }
        confirmVerified(owner, isCancelled)
    }

    @Test
    fun `unlock should release a held lock`() {
        every { owner.getAndSet(false) } returns true
        every { owner.get() } throws Exception("get() should not be called")
        every { owner.set(any()) } throws Exception("set() should not be called")
        spinLock.unlock()
        verify { owner.getAndSet(false) }
        confirmVerified(owner)
    }
}