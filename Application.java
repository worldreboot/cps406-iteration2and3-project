import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Application
{	
    private final static String TABLE_HEADERS[] = {"Name", "Length", "Artist", "Genre"};
    private static JTable table;
    private static JFrame display;

    private static User currentUser;
    private static Playlist currentPlaylist;

    private static Playlist databasePlaylist;

    private static ArrayList<User> listOfAllUsers;
    private static ArrayList<Playlist> currentListOfPlaylists;
    public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        initDefaults();
        initUI();
    }

    // set default values
    private static void initDefaults()
    {
        currentUser = new User();
        currentUser.setUsername("Bob");
        currentUser.addSongPlayed(new Song("Hi", 5, "Billy", "Screamo"));

        listOfAllUsers = new ArrayList<User>();
        listOfAllUsers.add(currentUser);
        listOfAllUsers.add(new User("beepis"));
        
        databasePlaylist = new Playlist( "Database" );

        currentPlaylist = databasePlaylist;
        databasePlaylist.addToPlaylist(new Song("Song 1", 1));
        databasePlaylist.addToPlaylist(new Song("Song 2", 2));
        databasePlaylist.addToPlaylist(new Song("Song 3", 3));
        databasePlaylist.addToPlaylist(new Song("Song 4", 4));
        databasePlaylist.addToPlaylist(new Song("Song 5", 5));
        databasePlaylist.addToPlaylist(new Song("Song 6", 6));
        databasePlaylist.addToPlaylist(new Song("Song 7", 7));
        databasePlaylist.addToPlaylist(new Song("Song 8", 8));
        databasePlaylist.addToPlaylist(new Song("Song 9", 9));
        databasePlaylist.addToPlaylist(new Song("Song 10", 10));

        currentListOfPlaylists = currentUser.getPlaylistList();
        //currentListOfPlaylists.add(databasePlaylist);
        //currentListOfPlaylists.add(testPlaylist);
    }

    private static void setCurrentPlaylist(Playlist playlist)
    {
        currentPlaylist = playlist;
    }

    private static void setCurrentListofPlaylists(ArrayList<Playlist> playlists)
    {
        currentPlaylist = databasePlaylist;
        currentListOfPlaylists = playlists;
    }

    private static void setCurrentUser(User user)
    {
        currentUser = user;
    }

    private static Playlist findPlaylistByName( String name )
    {
        for ( Playlist playlist : currentListOfPlaylists )
        {
            if ( playlist.getName() == name )
            {
                return playlist;
            }
        }
        return null;
    }

    private static User findUserByName( String name )
    {
        for ( User user : listOfAllUsers )
        {
            if ( user.getUsername() == name )
            {
                return user;
            }
        }
        return null;
    }

    private static void addNewUser( String name )
    {
        User newUser = new User( name );

        //currentUser.addPlaylist( newPlaylist );

        listOfAllUsers.add( newUser );

        setCurrentListofPlaylists( currentUser.getPlaylistList() );
    }

    private static void addNewPlaylist( String name )
    {
        Playlist newPlaylist = new Playlist( name );

        currentUser.addPlaylist( newPlaylist );

        setCurrentListofPlaylists( currentUser.getPlaylistList() );
    }

    private static void initUI()
    {	
        // Create window UI display
        display = new JFrame("Music Manager - CPS406")
        {
            {
                setSize(new Dimension(800, 600));
                setLayout(new BorderLayout());
                
                // Create top toolbar menu
                add(new JMenuBar()
                {
                    {
                        // Add "File" menu option
                        add(new JMenu("File")
                        {
                            {
                                // Add File options
                                add(new JMenuItem("User Stats")
                                {
                                    {
                                        addActionListener(new userStatsListener());
                                    }
                                    class userStatsListener implements ActionListener
                            		{
                            			// When button is pressed, clears then displays all checkout text
                            			public void actionPerformed(ActionEvent e)
                            			{
                            				String statsAsString = "Most Played Song: " + currentUser.getMostPlayedSong() + "\n"
                            				 + "Favorite Artist:        " + currentUser.getMostFreqArtist() + "\n"
                            				 + "Favorite Genre:       " + currentUser.getMostFreqGenre();
                            				
                            				JOptionPane.showMessageDialog(null, statsAsString, currentUser.getUsername() + " Stats", JOptionPane.INFORMATION_MESSAGE);
                            			}
                            		}
                                });
                                add(new JSeparator());
                                add(new JMenuItem("Exit")
                                {
                                    {
                                        addActionListener((x) -> System.exit(0));
                                    }
                                });
                            }
                        });
                    }
                }, BorderLayout.NORTH);

                // Create bottom toolbar menu
                add(new JMenuBar()
                {
                    {
                        add(new JLabel("User: "));
                        add(new JComboBox()
                        {
                            {
                                addItem( currentUser.getUsername() );
                                for (User user : listOfAllUsers)
                                {
                                    if ( user == currentUser ) continue;
                                    addItem( user.getUsername() );
                                }

                                addActionListener((x) -> 
                                {
                                    User user = findUserByName( (String) getSelectedItem() );
                                    setCurrentUser( user );
                                    setCurrentListofPlaylists( user.getPlaylistList() );
                                    display.dispose();
                                    initUI();
                                });
                            }
                        });

                        add(new JLabel("Currently viewing: "));
                        add(new JComboBox()
                        {
                            {
                                //System.out.println( currentPlaylist.getName() );
                                addItem( currentPlaylist.getName() );
                                for ( Playlist playlist : currentListOfPlaylists )
                                {
                                    if ( playlist == currentPlaylist ) continue;
                                    addItem( playlist.getName() );
                                }

                                if (databasePlaylist != currentPlaylist) addItem( databasePlaylist.getName() );

                                addActionListener((x) -> 
                                {
                                    String name = (String) getSelectedItem();
                                    if ( name.equals( "Database" ) )  setCurrentPlaylist( databasePlaylist );
                                    else setCurrentPlaylist( findPlaylistByName( name ) );
                                    display.dispose();
                                    initUI();
                                });
                            }
                        });

                        add(new JButton("New user")
                        {
                            {
                                addActionListener((x) ->
                                {
                                    addNewUser( JOptionPane.showInputDialog("Enter username for new user") );
                                    display.dispose();
                                    initUI();
                                });
                            }
                        });

                        add(new JButton("New playlist")
                        {
                            {
                                addActionListener((x) ->
                                {
                                    addNewPlaylist( JOptionPane.showInputDialog("Enter name for new playlist:") );
                                    display.dispose();
                                    initUI();
                                });
                            }
                        });
                    }
                }, BorderLayout.SOUTH);

                // Create centre table which displays songs in database/playlist
                table = new JTable(new DefaultTableModel(new Object[][]{}, TABLE_HEADERS)
                {
                    {
                        LinkedList<Song> songs = currentPlaylist.getPlaylist();
                        for ( Song song : songs )
                        {
                            addRow( new Object[] { song, song.getLength() + "", song.getArtist(), song.getGenre() } );
                        }
                    }
                });

                table.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseReleased(MouseEvent e)
                    {
                        if (!SwingUtilities.isRightMouseButton(e)) return;

                        int row = table.rowAtPoint(e.getPoint());
                        if ( row >= 0 && row < table.getRowCount() )
                        {
                            table.setRowSelectionInterval(row, row);
                        }
                        else
                        {
                            table.clearSelection();
                        }
                        //System.out.println( row );
                        String[] possibilities = new String[currentListOfPlaylists.size()];
                        for ( int i = 0; i < currentListOfPlaylists.size(); i++ ) { possibilities[i] = currentListOfPlaylists.get(i).getName(); }
                        String selectedPlaylist = (String) JOptionPane.showInputDialog(
                            display,
                            "",
                            "Select playlist to add song to...",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            possibilities,
                            "");
                        if ( selectedPlaylist == null ) return;
                        //System.out.println(selectedPlaylist);
                        findPlaylistByName( selectedPlaylist ).addToPlaylist( (Song) table.getModel().getValueAt( row, 0 ) );
                    }
                });

                add(new JScrollPane(table), BorderLayout.CENTER);
                //DefaultTableModel model = (DefaultTableModel) table.getModel();

                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                setVisible(true);
            }
        };
    }
}