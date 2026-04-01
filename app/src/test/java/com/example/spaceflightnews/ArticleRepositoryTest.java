package com.example.spaceflightnews;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.data.ArticleDao;
import com.example.spaceflightnews.data.SpaceFlightApiService;
import com.example.spaceflightnews.repository.ArticleRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class ArticleRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock ArticleDao mockDao;
    @Mock SpaceFlightApiService mockApi;
    @Mock Call mockCall;

    private ArticleRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        repository = new ArticleRepository(mockDao, mockApi);
    }

    @Test
    public void search_returnsDataFromDao() {
        Article article = new Article(1, "NASA News", "url", "summary", "2026", "url");

        MutableLiveData<List<Article>> liveData = new MutableLiveData<>();
        liveData.setValue(Collections.singletonList(article));

        when(mockDao.searchArticles("%NASA%")).thenReturn(liveData);
        when(mockApi.getArticles(anyString())).thenReturn(mockCall);

        LiveData<List<Article>> result = repository.search("NASA");

        List<Article> value = result.getValue(); // gracias a InstantTaskExecutorRule

        assertEquals(1, value.size());
        assertEquals("NASA News", value.get(0).title);
    }
}