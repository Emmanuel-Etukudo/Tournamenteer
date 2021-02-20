package ykim164cs242.tournamentor.Adapter.Admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ykim164cs242.tournamentor.Activity.Admin.AdminAddTournamentAddTeamActivity;
import ykim164cs242.tournamentor.Activity.Admin.EditAdminActivity;
import ykim164cs242.tournamentor.Activity.Common.StartMenuActivity;
import ykim164cs242.tournamentor.InformationStorage.TeamInfo;
import ykim164cs242.tournamentor.ListItem.AdminTeamListItem;
import ykim164cs242.tournamentor.ListItem.TeamListItem;
import ykim164cs242.tournamentor.R;

/**
 * AdminTeamListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_team_list_item.
 */

public class AdminTeamListAdapter extends BaseAdapter{

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private Context context;
    private List<AdminTeamListItem> teamList;

    public AdminTeamListAdapter(Context context, List<AdminTeamListItem> teamList) {
        this.context = context;
        this.teamList = teamList;
    }

    @Override
    public int getCount() {
        return teamList.size();
    }

    @Override
    public Object getItem(int position) {
        return teamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Matches each TextView with the corresponding id, and stores the right information
     * by using setText function. It retrieves the correct repositoryName, userName,
     * and description based on the position of the item in the repoList.
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = View.inflate(context, R.layout.activity_admin_team_list_item, null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        TextView teamName = (TextView) view.findViewById(R.id.team_name_item);
        TextView foundationYear = (TextView) view.findViewById(R.id.team_foundation_year_item);
        TextView captainName = (TextView) view.findViewById(R.id.team_captain_item);
        ImageView deleteTeam = (ImageView) view.findViewById(R.id.delete_team_button);

        // Set Texts for TextViews

        teamName.setText(teamList.get(position).getTeamName());
        foundationYear.setText(teamList.get(position).getFoundationYear());
        captainName.setText(teamList.get(position).getCaptainName());

        // Delete button: removes the team from the ListView and the DB

        deleteTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder yesNoBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View yesNoDialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                yesNoBuilder.setView(yesNoDialogView);
                yesNoBuilder.setTitle("Are you sure?");
                final AlertDialog yesNoDialog = yesNoBuilder.create();
                yesNoDialog.show();

                final TextView question = (TextView) yesNoDialogView.findViewById(R.id.yes_no_question);
                question.setText("Delete this team?");

                Button yesButton = (Button) yesNoDialogView.findViewById(R.id.yes_button);
                Button noButton = (Button) yesNoDialogView.findViewById(R.id.no_button);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Do action

                        rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments")
                                .child(teamList.get(position).getParticipatingTournament()).child("teams").child(teamList.get(position).getTeamName()).getRef().removeValue();
                        yesNoDialog.dismiss();
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        yesNoDialog.dismiss();

                    }
                });


            }
        });

        return view;
    }
}
