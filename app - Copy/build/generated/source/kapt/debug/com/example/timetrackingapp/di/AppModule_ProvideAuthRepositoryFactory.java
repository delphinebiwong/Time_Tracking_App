package com.example.timetrackingapp.di;

import com.example.timetrackingapp.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
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
public final class AppModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<FirebaseAuth> authProvider;

  public AppModule_ProvideAuthRepositoryFactory(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(authProvider.get());
  }

  public static AppModule_ProvideAuthRepositoryFactory create(
      javax.inject.Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideAuthRepositoryFactory(Providers.asDaggerProvider(authProvider));
  }

  public static AppModule_ProvideAuthRepositoryFactory create(Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideAuthRepositoryFactory(authProvider);
  }

  public static AuthRepository provideAuthRepository(FirebaseAuth auth) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthRepository(auth));
  }
}
