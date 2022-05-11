package pt.ipleiria.estg.dei.pi.mymultiprev.util

import android.view.View
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.snackbar.Snackbar
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource.Error
import java.time.format.DateTimeFormatter

object Util {
    fun formatDateTime(date: LocalDateTime): String {
        return date.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
    }

    @Composable
    fun <T> handleError(error: Error<T>) {
        when {
            error.isNetworkError -> Toast.makeText(
                LocalContext.current,
                "Por favor, verifique a sua conexão á internet",
                Toast.LENGTH_LONG
            )
            else -> Toast.makeText(LocalContext.current, "Ocorreu um erro", Toast.LENGTH_LONG)
        }
    }
//    fun <T> Fragment.handleError(error: Error<T>) {
//        when {
//            error.isNetworkError -> requireView().snackBar("Por favor, verifique a sua conexão á internet")
//            else -> requireView().snackBar("Ocorreu um erro")
//        }
//    }

    private fun View.snackBar(message: String) {
        val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        snackBar.show()
    }
}