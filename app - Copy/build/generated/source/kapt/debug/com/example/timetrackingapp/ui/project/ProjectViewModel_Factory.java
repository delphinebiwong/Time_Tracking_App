package com.example.timetrackingapp.ui.project;

import com.example.timetrackingapp.domain.repository.ProjectRepository;
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
public final class ProjectViewModel_Factory implements Factory<ProjectViewModel> {
  private final Provider<ProjectRepository> projectRepositoryProvider;

  public ProjectViewModel_Factory(Provider<ProjectRepository> projectRepositoryProvider) {
    this.projectRepositoryProvider = projectRepositoryProvider;
  }

  @Override
  public ProjectViewModel get() {
    return newInstance(projectRepositoryProvider.get());
  }

  public static ProjectViewModel_Factory create(
      javax.inject.Provider<ProjectRepository> projectRepositoryProvider) {
    return new ProjectViewModel_Factory(Providers.asDaggerProvider(projectRepositoryProvider));
  }

  public static ProjectViewModel_Factory create(
      Provider<ProjectRepository> projectRepositoryProvider) {
    return new ProjectViewModel_Factory(projectRepositoryProvider);
  }

  public static ProjectViewModel newInstance(ProjectRepository projectRepository) {
    return new ProjectViewModel(projectRepository);
  }
}
