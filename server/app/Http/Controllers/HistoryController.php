<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class HistoryController extends Controller
{
    public function index()
    {
        $histories = DB::table('history')->get();
        return response()->json($histories);
    }

    public function show($id)
    {
        $history = DB::table('history')->where('id', $id)->first();
        return response()->json($history);
    }

    public function store(Request $request)
    {
        $history = [
            'user_id' => $request->input('user_id'),
            'topic' => $request->input('topic'),
            'status' => $request->input('status', 'Completed')
        ];

        $historyId = DB::table('history')->insertGetId($history);
        $history['id'] = $historyId;

        return response()->json($history, 201);
    }

    public function update(Request $request, $id)
    {
        $history = [
            'user_id' => $request->input('user_id'),
            'topic' => $request->input('topic'),
            'status' => $request->input('status', 'Completed')
        ];

        DB::table('history')->where('id', $id)->update($history);
        $history['id'] = $id;

        return response()->json($history);
    }

    public function destroy($id)
    {
        DB::table('history')->where('id', $id)->delete();
        return response()->json(null, 204);
    }

    public function showByUserId($userId)
    {
        $histories = DB::table('history')->where('user_id', $userId)->get();
        return response()->json($histories);
    }
}
