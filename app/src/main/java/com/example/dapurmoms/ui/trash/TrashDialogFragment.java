package com.example.dapurmoms.ui.trash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TrashDialogFragment extends DialogFragment {

    public static TrashDialogFragment newInstance() {
        return new TrashDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_DapurMoms);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_trash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        // Adjust top padding to accommodate the status bar system window inset
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        toolbar.setNavigationOnClickListener(v -> dismiss());

        TrashPagerAdapter pagerAdapter = new TrashPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Pesanan");
                    break;
                case 1:
                    tab.setText("Belanja");
                    break;
                case 2:
                    tab.setText("Biaya");
                    break;
            }
        }).attach();
    }

    private static class TrashPagerAdapter extends FragmentStateAdapter {

        public TrashPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return TrashTabFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public static class TrashTabFragment extends Fragment {
        private static final String ARG_TYPE = "trash_type";

        private int type;
        private TrashViewModel viewModel;
        private TrashItemAdapter adapter;
        private RecyclerView rvTrash;
        private View layoutEmpty;

        public static TrashTabFragment newInstance(int type) {
            TrashTabFragment fragment = new TrashTabFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_TYPE, type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                type = getArguments().getInt(ARG_TYPE);
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_trash_tab, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            rvTrash = view.findViewById(R.id.rv_trash);
            layoutEmpty = view.findViewById(R.id.layout_empty);

            viewModel = new ViewModelProvider(this).get(TrashViewModel.class);

            adapter = new TrashItemAdapter(type, item -> {
                if (type == TrashItemAdapter.TYPE_PESANAN) {
                    Pesanan p = (Pesanan) item;
                    viewModel.restorePesanan(p.getId());
                    showSnackbar("Pesanan dari " + (p.getNamaPemesan().equals("-") ? "Pelanggan" : p.getNamaPemesan()) + " dipulihkan");
                } else if (type == TrashItemAdapter.TYPE_BELANJA) {
                    BelanjaBahan b = (BelanjaBahan) item;
                    viewModel.restoreBelanja(b.getId());
                    showSnackbar("Belanja " + b.getNamaBahan() + " dipulihkan");
                } else if (type == TrashItemAdapter.TYPE_BIAYA) {
                    BiayaLain c = (BiayaLain) item;
                    viewModel.restoreBiaya(c.getId());
                    showSnackbar("Biaya " + c.getKeterangan() + " dipulihkan");
                }
            });

            rvTrash.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvTrash.setAdapter(adapter);

            if (type == TrashItemAdapter.TYPE_PESANAN) {
                viewModel.getDeletedPesanan().observe(getViewLifecycleOwner(), list -> {
                    adapter.setData(list);
                    updateEmptyState(list == null || list.isEmpty());
                });
            } else if (type == TrashItemAdapter.TYPE_BELANJA) {
                viewModel.getDeletedBelanja().observe(getViewLifecycleOwner(), list -> {
                    adapter.setData(list);
                    updateEmptyState(list == null || list.isEmpty());
                });
            } else if (type == TrashItemAdapter.TYPE_BIAYA) {
                viewModel.getDeletedBiaya().observe(getViewLifecycleOwner(), list -> {
                    adapter.setData(list);
                    updateEmptyState(list == null || list.isEmpty());
                });
            }
        }

        private void updateEmptyState(boolean isEmpty) {
            if (isEmpty) {
                rvTrash.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                rvTrash.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        }

        private void showSnackbar(String message) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
