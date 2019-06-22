package top.soyask.calendarii.ui.adapter.memorail;

import android.content.res.Resources;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import top.soyask.calendarii.R;
import top.soyask.calendarii.entity.MemorialDay;
import top.soyask.calendarii.global.Global;

public class MemorialDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MemorialDay> mMemorialDays;
    private MemorialDayActionListener mMemorialDayActionListener;

    public MemorialDayAdapter(List<MemorialDay> memorialDays, MemorialDayActionListener listener) {
        this.mMemorialDays = memorialDays;
        this.mMemorialDayActionListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_item_memorial, parent, false);
        return new MemorialDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MemorialDay memorialDay = mMemorialDays.get(position);
        String who = memorialDay.getWho();
        String title = getTitle(memorialDay, who, holder.itemView.getResources());
        String details = memorialDay.getDetails();
        String date = holder.itemView.getResources()
                .getString(R.string.date_format, memorialDay.getMonth(), memorialDay.getDay());
        String lunar = memorialDay.getLunar();
        if (memorialDay.isLunar()) {
            date += " " + lunar;
        } else {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            int year = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(0);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, memorialDay.getMonth() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, memorialDay.getDay());
            long days = calendar.getTimeInMillis() / DateUtils.DAY_IN_MILLIS;
            long currentDays = System.currentTimeMillis() / DateUtils.DAY_IN_MILLIS;
            int delta = Long.valueOf(currentDays - days).intValue();
            if (delta < 0) {
                date += String.format(Locale.getDefault(), " 还有%d天", -delta);
            } else if (delta > 0) {
                date += String.format(Locale.getDefault(), " 已过了%d天", delta);
            }
        }
        MemorialDayViewHolder viewHolder = (MemorialDayViewHolder) holder;
        viewHolder.tvTitle.setText(title);
        viewHolder.tvDate.setText(date);
        if (details == null || details.isEmpty()) {
            viewHolder.tvDetails.setVisibility(View.GONE);
        } else {
            viewHolder.tvDetails.setText(details);
            viewHolder.tvDetails.setVisibility(View.VISIBLE);
        }

        viewHolder.itemView.setOnClickListener(
                v -> mMemorialDayActionListener.onMemorialDayClick(position, memorialDay));
        viewHolder.itemView.setOnLongClickListener(
                v -> mMemorialDayActionListener.onMemorialDayLongClick(position, memorialDay));
    }

    private String getTitle(MemorialDay memorialDay, String who, Resources resources) {
        String[] whos = who.split(Global.FLAG);
        StringBuilder title = new StringBuilder();
        for (int i = 0; i < whos.length; i++) {
            if (i > 0 && i == whos.length - 1) {
                title.append(resources.getString(R.string.and));
            } else if (i > 0) {
                title.append(resources.getString(R.string.name_separator));
            }
            title.append(whos[i]);
        }
        String name = memorialDay.getName();
        return title.append(resources.getString(R.string.of)).append(name).toString();
    }

    @Override
    public int getItemCount() {
        return mMemorialDays.size();
    }

    public interface MemorialDayActionListener {
        void onMemorialDayClick(int position, MemorialDay day);

        boolean onMemorialDayLongClick(int position, MemorialDay day);
    }
}
