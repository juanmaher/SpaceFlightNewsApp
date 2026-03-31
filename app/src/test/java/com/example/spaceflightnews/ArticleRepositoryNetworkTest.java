package com.example.spaceflightnews;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.data.ArticleDao;
import com.example.spaceflightnews.data.ArticleResponse;
import com.example.spaceflightnews.data.SpaceFlightApiService;
import com.example.spaceflightnews.repository.ArticleRepository;

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
        // Arrange: Simulamos una respuesta exitosa de la API
        ArticleResponse fakeResponse = new ArticleResponse();
        fakeResponse.results = Arrays.asList(new Article(1, "Test", "url", "sum", "date"));

        // Mockeamos el comportamiento de Retrofit (enqueue)
        when(mockApi.getArticles()).thenReturn(mockCall);

        // Usamos un ArgumentCaptor para capturar el Callback que el repo pasa a Retrofit
        ArgumentCaptor<Callback<ArticleResponse>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        // Act
        repository.syncArticles();

        // Assert: Verificamos que se llamó a la API
        verify(mockApi).getArticles();
        verify(mockCall).enqueue(callbackCaptor.capture());

        // Simulamos que la respuesta llega ahora
        callbackCaptor.getValue().onResponse(mockCall, Response.success(fakeResponse));

        // Verificamos que el DAO recibió la orden de insertar (usamos timeout por el Thread)
        verify(mockDao, timeout(1000)).insertArticles(fakeResponse.results);
    }
}
