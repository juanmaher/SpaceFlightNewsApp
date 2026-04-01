package com.example.spaceflightnews;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.data.ArticleDao;
import com.example.spaceflightnews.data.ArticleResponse;
import com.example.spaceflightnews.data.SpaceFlightApiService;
import com.example.spaceflightnews.repository.ArticleRepository;
import com.example.spaceflightnews.repository.RepositoryCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class ArticleRepositoryNetworkTest {

    @Mock
    private ArticleDao mockDao;
    @Mock private SpaceFlightApiService mockApi;
    @Mock private Call<ArticleResponse> mockCall;

    private ArticleRepository repository;

    @Before
    public void setup() {
        repository = new ArticleRepository(mockDao, mockApi);
    }

    @Test
    public void syncArticles_onApiResponse_savesToDb() {
        ArticleResponse fakeResponse = new ArticleResponse();
        fakeResponse.results = Arrays.asList(new Article(1, "Test", "url", "sum", "date", "url"));

        when(mockApi.getArticles()).thenReturn(mockCall);

        ArgumentCaptor<Callback<ArticleResponse>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        repository.syncArticles(any(RepositoryCallback.class));

        verify(mockApi).getArticles();
        verify(mockCall).enqueue(callbackCaptor.capture());

        callbackCaptor.getValue().onResponse(mockCall, Response.success(fakeResponse));

        verify(mockDao, timeout(1000)).insertArticles(fakeResponse.results);
    }
}
