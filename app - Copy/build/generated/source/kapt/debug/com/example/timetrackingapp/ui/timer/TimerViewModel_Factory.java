package com.example.timetrackingapp.ui.timer;

import android.content.Context;
import com.example.timetrackingapp.domain.repository.HistoryRepository;
import com.example.timetrackingapp.domain.repository.ProjectRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class TimerViewModel_Factory implements Factory<TimerViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<ProjectRepository> projectRepositoryProvider;

  public TimerViewModel_Factory(Provider<Context> contextProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<ProjectRepository> projectRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.projectRepositoryProvider = projectRepositoryProvider;
  }

  @Override
  public TimerViewModel get() {
    return newInstance(contextProvider.get(), historyRepositoryProvider.get(), projectRepositoryProvider.get());
  }

  public static TimerViewModel_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<HistoryRepository> historyRepositoryProvider,
      javax.inject.Provider<ProjectRepository> projectRepositoryProvider) {
    return new TimerViewModel_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(historyRepositoryProvider), Providers.asDaggerProvider(projectRepositoryProvider));
  }

  public static TimerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<ProjectRepository> projectRepositoryProvider) {
    return new TimerViewModel_Factory(contextProvider, historyRepositoryProvider, projectRepositoryProvider);
  }

  public static TimerViewModel newInstance(Context context, HistoryRepository historyRepository,
      ProjectRepository projectRepository) {
    return new TimerViewModel(context, historyRepository, projectRepository);
  }
}
