/*
 * Copyright 2013 Evident Solutions Oy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program. If not, see <​http://www.gnu.org/licenses/>.
 */

package fi.evident.elasticsearch.voikko.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.AnalysisSettingsRequired;
import org.elasticsearch.index.settings.IndexSettings;
import org.puimula.libvoikko.Voikko;

@AnalysisSettingsRequired
public final class VoikkoTokenFilterFactory extends AbstractTokenFilterFactory {

    private final Voikko voikko;

    private final VoikkoTokenFilterConfiguration cfg = new VoikkoTokenFilterConfiguration();

    @Inject
    public VoikkoTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);

        String language = settings.get("language", "fi_FI");
        String dictionaryPath = settings.get("dictionaryPath");

        for (String dir : settings.getAsArray("libraryPath"))
            Voikko.addLibraryPath(dir);

        voikko = new Voikko(language, dictionaryPath);
        cfg.analyzeAll = settings.getAsBoolean("analyzeAll", cfg.analyzeAll);
        cfg.minimumWordSize = settings.getAsInt("minimumWordSize", cfg.minimumWordSize);
        cfg.maximumWordSize = settings.getAsInt("maximumWordSize", cfg.maximumWordSize);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new VoikkoTokenFilter(tokenStream, voikko, cfg);
    }
}
