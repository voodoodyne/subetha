# SubEtha

Note that this repoistory is quite old and was imperfectly migrated from Google Code. Most of the documentation is in the `wiki` directory. Prepackaged binaries have been lost to time. However, the code works (use Resin v4.0.25).

-----


SubEtha is a modern, sophisticated mailing list manager.

 * Easy installation on Windows and Unix platforms (see the InstallGuide)
 * A user-friendly web interface for all configuration management
 * Virtual domains (ie list@foo.com and list@bar.com are separate lists)
 * Searchable, threaded archives (see the ScreenShots)
 * Users can have multiple email addresses and self-moderate messages from unknown addresses (see the ScreenShots)
 * Intelligent attachment handling; attachments can be removed from delivered mail and replaced with a download link to the archives
 * Pluggable, configurable message processing filters which can arbitrarily modify the inbound and outbound message streams. Functional included filters are leave attachments on server, strip attachments, reply to, hold all mail, add list headers, append footer, subject modification. Other uses for filters: spam detection and insertion of advertising
 * Per-list role-based permissions (see the ScreenShots)
 * One-step creation of basic list types (ie 'Announce-Only List' or 'Technical Support List'). The set of available types is pluggable
 * Users can compose and reply to messages from the web interface
 * Intelligent VERP bounce processing
 * Clusterable for nearly unlimited scalability
 * Easy integration with any mail transport agent (see ReceivingMail).
 * International characters in emails are properly passed through the system and rendered in the web interface (see [I18N])
 * Bookmarkable URLs
 * A modular SMTP library that can be used outside SubEtha - see [https://github.com/voodoodyne/subethasmtp SubEthaSMTP]
