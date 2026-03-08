package com.example.timetrackingapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class FirebaseProjectRepository_Factory implements Factory<FirebaseProjectRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  public FirebaseProjectRepository_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
  }

  @Override
  public FirebaseProjectRepository get() {
    return newInstance(firestoreProvider.get(), authProvider.get());
  }

  public static FirebaseProjectRepository_Factory create(
      javax.inject.Provider<FirebaseFirestore> firestoreProvider,
      javax.inject.Provider<FirebaseAuth> authProvider) {
    return new FirebaseProjectRepository_Factory(Providers.asDaggerProvider(firestoreProvider), Providers.asDaggerProvider(authProvider));
  }

  public static FirebaseProjectRepository_Factory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    return new FirebaseProjectRepository_Factory(firestoreProvider, authProvider);
  }

  public static FirebaseProjectRepository newInstance(FirebaseFirestore firestore,
      FirebaseAuth auth) {
    return new FirebaseProjectRepository(firestore, auth);
  }
}
