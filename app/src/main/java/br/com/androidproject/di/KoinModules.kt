package br.com.androidproject.di

import androidx.room.Room
import br.com.androidproject.authentication.FirebaseAuthRepository
import br.com.androidproject.database.AndroidProjectDB
import br.com.androidproject.repository.RouteRepository
import br.com.androidproject.ui.viewmodels.HistoryViewModel
import br.com.androidproject.ui.viewmodels.SignInViewModel
import br.com.androidproject.ui.viewmodels.SignUpViewModel
import br.com.androidproject.ui.viewmodels.MapViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::HistoryViewModel)



}

val storageModule = module {
 //   singleOf(::TasksRepository)
//    singleOf(::UsersRepository)
    singleOf(::FirebaseAuthRepository)
    singleOf(::RouteRepository)
    single {
        Room.databaseBuilder(
            androidContext(),
            AndroidProjectDB::class.java, "androidproject.db"
        ).build()
    }
    single {
        get<AndroidProjectDB>().routeDao()
    }

    single {
        FirebaseFirestore.getInstance()
    }


}
val firebaseModule = module {
    single {
        Firebase.auth
    }
}