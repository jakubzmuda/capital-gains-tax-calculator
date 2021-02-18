import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class TestCheck {

    @Test
    fun `should pass`() {
        assertThat(1).isEqualTo(1)
    }
}
