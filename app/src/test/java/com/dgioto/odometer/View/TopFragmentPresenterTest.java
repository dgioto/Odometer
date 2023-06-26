package com.dgioto.odometer.View;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dgioto.odometer.R;
import com.dgioto.odometer.Service.OdometerService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TopFragmentPresenterTest {

    @Mock
    private TopFragmentContract.View mockView;
    @Mock
    private Context mockContext;
    @Mock
    private LocationManager mockLocationManager;
    @Mock
    private PackageManager mockPackageManager;
    private TopFragmentPresenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new TopFragmentPresenter(mockView, mockContext);
    }

    @Test
    public void testGetBound() {
        presenter.setBound(true);
        assertTrue(presenter.getBound());

        presenter.setBound(false);
        assertFalse(presenter.getBound());
    }

    @Test
    public void testSetBound() {
        presenter.setBound(true);
        assertTrue(presenter.getBound());
    }

    @Test
    public void testGetConnection() {
        assertNotNull(presenter.getConnection());
    }

    @Test
    public void testOnClickStartWithPermissionGranted() {
        when(ContextCompat.checkSelfPermission(mockContext, OdometerService.PERMISSION_STRING))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.LOCATION_SERVICE)).thenReturn(mockLocationManager);
        when(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);

        presenter.onClickStart();

        verify(mockView).getNoteButton().setVisibility(View.VISIBLE);
        verify(mockView).getDischargeButton().setVisibility(View.VISIBLE);
        verify(mockView).getStartButton().setText(R.string.restart);
    }

    @Test
    public void testOnClickStartWithPermissionDenied() {
        when(ContextCompat.checkSelfPermission(mockContext, OdometerService.PERMISSION_STRING))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        presenter.onClickStart();

        verify(mockView, Mockito.never()).getNoteButton().setVisibility(View.VISIBLE);
        verify(mockView, Mockito.never()).getDischargeButton().setVisibility(View.VISIBLE);
        verify(mockView, Mockito.never()).getStartButton().setText(R.string.restart);
    }
}