# Space Flight News App

An Android application designed to keep updated with the latest news from the space industry. This app utilizes the Space Flight News API to provide users with detailed information about spaceflight-related articles.

## Data flow diagrams

1. Initial data fetch (App Startup)

The following diagram illustrates how the application synchronizes with the API when it is first launched to ensure the local cache is up to date.

```mermaid
sequenceDiagram
    participant UI as MainActivity
    participant VM as ArticleViewModel
    participant Repo as ArticleRepository
    participant API as SpaceFlightApiService
    participant DB as ArticleDao (Room)

    Note over UI, DB: App Launch
    UI->>VM: fetchArticles()
    VM->>VM: setStatus(LOADING)
    VM->>Repo: syncArticles(callback)
    Repo->>API: getArticles()
    
    API-->>Repo: Response (JSON Articles)
    
    Repo->>Repo: processAndSave(results)
    Note right of Repo: Background Thread
    Repo->>DB: insertArticles(list)
    DB-->>Repo: Success
    Repo-->>VM: callback.onSuccess()
    VM->>VM: setStatus(SUCCESS)
    
    Note over UI, DB: UI updates automatically via LiveData Observer
    DB-->>UI: LiveData<List<Article>> updated
```

2. Search Query Execution

This diagram shows the parallel flow during a search: returning immediate local results for a better user experience while refreshing the data from the network.

```mermaid
sequenceDiagram
    participant UI as MainActivity
    participant VM as ArticleViewModel
    participant Repo as ArticleRepository
    participant API as SpaceFlightApiService
    participant DB as ArticleDao (Room)

    UI->>UI: onQueryTextSubmit(query)
    UI->>VM: searchArticles(query)
    VM->>VM: setStatus(LOADING)
    
    VM->>Repo: search(query, callback)
    
    par Network Refresh
        Repo->>API: getArticles(query)
        API-->>Repo: Response (Filtered JSON)
        Repo->>DB: insertArticles(results)
        Repo-->>VM: callback.onSuccess()
        VM->>VM: setStatus(SUCCESS)
    and Local DB Return
        Repo->>DB: searchArticles("%query%")
        DB-->>UI: Return LiveData<List>
    end

    Note over UI, DB: If Network fails, UI still shows Local Results
```

## Class diagram
```mermaid
classDiagram
    class SpaceFlightApplication {
        +getCompositionRoot() CompositionRoot
    }

    class CompositionRoot {
        +ArticleRepository mRepository
        +ViewModelProvider.Factory mFactory
    }

    class ArticleViewModel {
        -ArticleRepository mRepository
        -MutableLiveData<Resource> mArticlesStatus
        +fetchArticles()
        +searchArticles(query)
        +getRecentArticles() LiveData
    }

    class ArticleRepository {
        -ArticleDao mArticleDao
        -SpaceFlightApiService mApiService
        +syncArticles(callback)
        +search(query, callback) LiveData
        +getArticleById(id) LiveData
        +getRecentArticles() LiveData
        -refreshArticlesByQuery(query, callback)
        -executeRequest(call, callback)
        -processAndSave(articles, callback)
    }

    class ArticleDao {
        <<interface>>
        +insertArticles(list)
        +getRecentArticles() LiveData
        +searchArticles(query) LiveData
        +getArticleById(id) LiveData
    }

    class SpaceFlightApiService {
        <<interface>>
        +getArticles() Call
        +getArticles(query) Call
    }

    class Article {
        +int id
        +String title
        +String summary
        +String imageUrl
        +String publishedAt
        +String url
    }

    class Resource {
        +Status status
        +T data
        +String message
    }

    SpaceFlightApplication --> CompositionRoot
    CompositionRoot --> ArticleRepository
    ArticleViewModel --> ArticleRepository
    ArticleViewModel ..> Resource
    ArticleRepository --> ArticleDao
    ArticleRepository --> SpaceFlightApiService
    ArticleDao ..> Article
    ArticleRepository ..> Article
```