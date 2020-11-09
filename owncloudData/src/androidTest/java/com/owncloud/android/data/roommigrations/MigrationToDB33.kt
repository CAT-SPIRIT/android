/**
 *   ownCloud Android client application
 *
 *   @author Abel García de Prada
 *   Copyright (C) 2020 ownCloud GmbH.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.owncloud.android.data.roommigrations

import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.filters.SmallTest
import com.owncloud.android.data.OwncloudDatabase
import com.owncloud.android.data.ProviderMeta.ProviderTableMeta.OCSHARES_TABLE_NAME
import com.owncloud.android.data.sharing.shares.datasources.mapper.OCShareMapper
import com.owncloud.android.testutil.OC_SHARE
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test the migration from database to version 29.
 */
@SmallTest
class MigrationToDB33 : MigrationTest() {
    private val shareMapper: OCShareMapper = OCShareMapper()

    @Test
    fun migrationFrom32to33_containsCorrectData() {
        performMigrationTest(
            previousVersion = DB_VERSION_32,
            currentVersion = DB_VERSION_33,
            insertData = { database -> insertDataToTest(database) },
            validateMigration = { database -> validateMigrationTo33(database) },
            listOfMigrations = OwncloudDatabase.ALL_MIGRATIONS
        )
    }

    @Test
    fun startInVersion33_containsCorrectData() {
        performMigrationTest(
            previousVersion = DB_VERSION_33,
            currentVersion = DB_VERSION_33,
            insertData = { database -> insertDataToTest(database) },
            validateMigration = { Unit },
            listOfMigrations = arrayOf()
        )
    }

    private fun insertDataToTest(database: SupportSQLiteDatabase) {
        database.run {
            insert(
                OCSHARES_TABLE_NAME,
                SQLiteDatabase.CONFLICT_NONE,
                shareMapper.toEntity(OC_SHARE)!!.toContentValues()
            )
            insert(
                OCSHARES_TABLE_NAME,
                SQLiteDatabase.CONFLICT_NONE,
                shareMapper.toEntity(OC_SHARE.copy(id = 499))!!.toContentValues()
            )
            close()
        }
    }

    private fun validateMigrationTo33(database: SupportSQLiteDatabase) {
        val sharesCount = getCount(database, OCSHARES_TABLE_NAME)
        assertEquals(2, sharesCount)
        database.close()
    }
}
