package com.dgioto.odometer.View;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TopFragmentPresenterTest {

    @Mock
    private TopFragmentContract.View mockView;
    @Mock
    private Context mockContext;
    @Mock
    private LocationManager mockLocationManager;

    private TopFragmentPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter = new TopFragmentPresenter(mockView, mockContext);
        presenter.manager = mockLocationManager;
    }

    @Test
    public void testGetBound() {
        presenter.setBound(true);
        assertTrue(presenter.getBound());

        presenter.setBound(false);
        assertFalse(presenter.getBound());
    }

    @Test
    public void testGetConnection() {
        assertNotNull(presenter.getConnection());
    }
}