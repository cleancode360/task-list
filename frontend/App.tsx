import { useCallback, useEffect, useState } from "react";
import { ActivityIndicator, View } from "react-native";
import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { SafeAreaProvider } from "react-native-safe-area-context";

import AuthScreen from "./src/screens/AuthScreen";
import TaskListScreen from "./src/screens/TaskListScreen";
import TaskDetailScreen from "./src/screens/TaskDetailScreen";
import TagScreen from "./src/screens/TagScreen";
import { auth } from "./src/api/client";

export type MainTabParamList = {
  TasksTab: undefined;
  TagsTab: undefined;
};

export type TaskStackParamList = {
  TaskList: undefined;
  TaskDetail: { id: number };
};

const Tab = createBottomTabNavigator<MainTabParamList>();
const TaskStack = createNativeStackNavigator<TaskStackParamList>();

function TasksNavigator() {
  return (
    <TaskStack.Navigator>
      <TaskStack.Screen name="TaskList" component={TaskListScreen} options={{ title: "Tasks" }} />
      <TaskStack.Screen name="TaskDetail" component={TaskDetailScreen} options={{ title: "Task" }} />
    </TaskStack.Navigator>
  );
}

function MainTabs() {
  return (
    <Tab.Navigator screenOptions={{ headerShown: false }}>
      <Tab.Screen name="TasksTab" component={TasksNavigator} options={{ title: "Tasks" }} />
      <Tab.Screen name="TagsTab" component={TagScreen} options={{ title: "Tags" }} />
    </Tab.Navigator>
  );
}

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    auth.me().then((data) => {
      setIsLoggedIn(!!data?.username);
      setLoading(false);
    });
  }, []);

  const handleLoginSuccess = useCallback(() => {
    setIsLoggedIn(true);
  }, []);

  const handleLogout = useCallback(async () => {
    await auth.logout();
    setIsLoggedIn(false);
  }, []);

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        {isLoggedIn ? (
          <MainTabs />
        ) : (
          <AuthScreen onLoginSuccess={handleLoginSuccess} />
        )}
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
