package com.example.timetrackingapp.di;

import com.example.timetrackingapp.domain.repository.HistoryRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideHistoryRepositoryFactory implements Factory<HistoryRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  public AppModule_ProvideHistoryRepositoryFactory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
  }

  @Override
  public HistoryRepository get() {
    return provideHistoryRepository(firestoreProvider.get(), authProvider.get());
  }

  public static AppModule_ProvideHistoryRepositoryFactory create(
      javax.inject.Provider<FirebaseFirestore> firestoreProvider,
      javax.inject.Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideHistoryRepositoryFactory(Providers.asDaggerProvider(firestoreProvider), Providers.asDaggerProvider(authProvider));
  }

  public static AppModule_ProvideHistoryRepositoryFactory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideHistoryRepositoryFactory(firestoreProvider, authProvider);
  }

  public static HistoryRepository provideHistoryRepository(FirebaseFirestore firestore,
      FirebaseAuth auth) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHistoryRepository(firestore, auth));
  }
}
