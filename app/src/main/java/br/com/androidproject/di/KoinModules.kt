package br.com.androidproject.di

import androidx.room.Room
import br.com.androidproject.authentication.FirebaseAuthRepository
import br.com.androidproject.ui.viewmodels.SignInViewModel
import br.com.androidproject.ui.viewmodels.SignUpViewModel
import br.com.androidproject.ui.viewmodels.MapViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::MapViewModel)
}

val storageModule = module {
 //   singleOf(::TasksRepository)
//    singleOf(::UsersRepository)
    singleOf(::FirebaseAuthRepository)
/*    single {
        Room.databaseBuilder(
            androidContext(),
            MinhasTarefasDatabase::class.java, "minhas-tarefas.db"
        ).build()
    }
    single {
        get<MinhasTarefasDatabase>().taskDao()
    }

 */
}
val firebaseModule = module {
    single {
        Firebase.auth
    }
}